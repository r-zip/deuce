(ns deuce.emacs.ccl
  (:use [deuce.emacs-lisp :only (defun defvar)])
  (:require [clojure.core :as c])
  (:refer-clojure :exclude []))

(defvar code-conversion-map-vector nil
  "Vector of code conversion maps.")

(defvar font-ccl-encoder-alist nil
  "Alist of fontname patterns vs corresponding CCL program.
  Each element looks like (REGEXP . CCL-CODE),
   where CCL-CODE is a compiled CCL program.
  When a font whose name matches REGEXP is used for displaying a character,
   CCL-CODE is executed to calculate the code point in the font
   from the charset number and position code(s) of the character which are set
   in CCL registers R0, R1, and R2 before the execution.
  The code point in the font is set in CCL registers R1 and R2
   when the execution terminated.
   If the font is single-byte font, the register R2 is not used.")

(defvar translation-hash-table-vector nil
  "Vector containing all translation hash tables ever defined.
  Comprises pairs (SYMBOL . TABLE) where SYMBOL and TABLE were set up by calls
  to `define-translation-hash-table'.  The vector is indexed by the table id
  used by CCL.")

(defun register-ccl-program (name ccl-prog)
  "Register CCL program CCL-PROG as NAME in `ccl-program-table'.
  CCL-PROG should be a compiled CCL program (vector), or nil.
  If it is nil, just reserve NAME as a CCL program name.
  Return index number of the registered CCL program."
  )

(defun ccl-execute (ccl-prog reg)
  "Execute CCL-PROGRAM with registers initialized by REGISTERS.

  CCL-PROGRAM is a CCL program name (symbol)
  or compiled code generated by `ccl-compile' (for backward compatibility.
  In the latter case, the execution overhead is bigger than in the former).
  No I/O commands should appear in CCL-PROGRAM.

  REGISTERS is a vector of [R0 R1 ... R7] where RN is an initial value
  for the Nth register.

  As side effect, each element of REGISTERS holds the value of
  the corresponding register after the execution.

  See the documentation of `define-ccl-program' for a definition of CCL
  programs."
  )

(defun ccl-program-p (object)
  "Return t if OBJECT is a CCL program name or a compiled CCL program code.
  See the documentation of `define-ccl-program' for the detail of CCL program."
  )

(defun register-code-conversion-map (symbol map)
  "Register SYMBOL as code conversion map MAP.
  Return index number of the registered map."
  )

(defun ccl-execute-on-string (ccl-program status string &optional continue unibyte-p)
  "Execute CCL-PROGRAM with initial STATUS on STRING.

  CCL-PROGRAM is a symbol registered by `register-ccl-program',
  or a compiled code generated by `ccl-compile' (for backward compatibility,
  in this case, the execution is slower).

  Read buffer is set to STRING, and write buffer is allocated automatically.

  STATUS is a vector of [R0 R1 ... R7 IC], where
   R0..R7 are initial values of corresponding registers,
   IC is the instruction counter specifying from where to start the program.
  If R0..R7 are nil, they are initialized to 0.
  If IC is nil, it is initialized to head of the CCL program.

  If optional 4th arg CONTINUE is non-nil, keep IC on read operation
  when read buffer is exhausted, else, IC is always set to the end of
  CCL-PROGRAM on exit.

  It returns the contents of write buffer as a string,
   and as side effect, STATUS is updated.
  If the optional 5th arg UNIBYTE-P is non-nil, the returned string
  is a unibyte string.  By default it is a multibyte string.

  See the documentation of `define-ccl-program' for the detail of CCL program."
  )