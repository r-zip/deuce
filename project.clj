(defproject deuce "0.1.0-SNAPSHOT"
  :description "DEUCE - Deuce is (not yet) Emacs under Clojure"
  :license {:name "GNU General Public License Version 3"
            :url "http://www.gnu.org/licenses/"
            :distribution :repo}
  :url "http://hraberg.github.io/deuce/"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [com.googlecode.lanterna/lanterna "3.0.0-beta2" :exclusions [junit]]
                 [com.taoensso/timbre "4.3.1"]
                 [org.tcrawley/dynapath "0.2.3"]
                 [org.flatland/ordered "1.5.3"]
                 [fipp "0.5.1"]]
  :plugins [[lein-difftest "2.0.0"]
            [lein-marginalia "0.9.0"]
            [lein-codox "0.9.5"]]
  :profiles {:uberjar {:aot :all
                       :auto-clean false}
             :codox {:pedantic? :ranges
                     :codox {:output-path "docs/codox"
                             :namespaces [#"^(?!deuce\.emacs$).*$"]}}
             :test {:global-vars {*warn-on-reflection* true}}}
  :pedantic? :abort
  :resource-paths ["emacs/lisp" "resources"]
  :jar-exclusions [#".*\.elc" #"TUTORIAL\..*"
                   #"ChangeLog.*" #"Makefile.*" #"README" #"\.gitignore"]
  :jvm-opts ^:replace ["-Xss4m" "-Xmx1g"]
  :main deuce.main)
