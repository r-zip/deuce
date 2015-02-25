'use strict';

// The example from https://github.com/Matt-Esch/virtual-dom split into a server rendering / client patching spike.
// virtual-dom isn't really used well, as all patching is done in text and the tree is then recreated client side.

const diff = require('diff'),
      ws = require('ws'),
      fs = require('fs'),
      url = require('url'),
      path = require('path'),
      jsdom = require('jsdom').jsdom,
      DiffDom = require('diff-dom'),
      jsonpatch = require('fast-json-patch');

let serialize = JSON.stringify,
    deserialize = JSON.parse;

function toDom(root, h) {
    root.innerHTML = h;
    return root;
}

function render(s) {
    let count = s.count;
    return '<div style=\"text-align:center;line-height:' + (100 + count) +
        'px;border:1px solid red;width:' + (100 + count) + 'px;height:' +
        (100 + count) + 'px;\">' + count + '</div>';
}

let state = {count: 0},
    document = jsdom(),
    html = render(state),
    tree = toDom(document.createElement('body'), html),
    revision = 0,
    connections = [],
    dd = new DiffDom();

ws.createServer({port: 8080}, (ws) => {
    let parameters = url.parse(ws.upgradeReq.url, true).query;
    connections.push({ws: ws,
                      dom: JSON.parse(parameters.dom),
                      json: JSON.parse(parameters.json)});
    let id = connections.length - 1,
        onrefresh = () => {
            fs.open(path.join(__dirname, 'vd-client-bundle.js'), 'r', (err, fd) => {
                if (err) {
                    throw (err);
                }
                let data = serialize(['r', revision, html, state, fs.fstatSync(fd).mtime, Date.now()]);
                console.log(' refresh:', data);
                ws.send(data);
            });
        };
    ws.on('close', () => {
        console.log('disconnect:', id);
        connections.splice(id, 1);
    });
    ws.on('message', (data) => {
        let message = deserialize(data);
        ({r: onrefresh})[message[0]].apply(null, message.slice(1));
    });
    console.log('new client:', id);
    onrefresh();
});

function toSimpleCharDiff(d) {
    if (d.added) {
        return d.value;
    }
    if (d.removed) {
        return -d.value.length;
    }
    return d.value.length;
}

setInterval(() => {
    let startTime = Date.now(),
        newState = {count: state.count + 1},
        newHtml = render(newState);
    console.log('rendered:', newHtml);

    if (connections.length > 0) {
        let charData, domData, jsonData;
        connections.forEach((c) => {
            let data;
            console.time('    diff');
            if (c.json) {
                if (!jsonData) {
                    jsonData = serialize(['j', revision, jsonpatch.compare(state, newState), startTime]);
                }
                data = jsonData;
            } else if (c.dom) {
                if (!domData) {
                    let diffs, newTree;
                    newTree = toDom(document.createElement('body'), newHtml);
                    diffs = dd.diff(tree, newTree);
                    domData = serialize(['d', revision, diffs, startTime]);
                    tree = newTree;
                }
                data = domData;
            } else {
                if (!charData) {
                    let diffs = diff.diffChars(html, newHtml).map(toSimpleCharDiff);
                    charData = serialize(['c', revision, diffs, startTime]);
                }
                data = charData;
            }
            console.timeEnd('    diff');
            console.log(' sending:', data);
            c.ws.send(data);
        });
    }

    revision = revision + 1;
    state = newState;
    html = newHtml;
}, 1000);
