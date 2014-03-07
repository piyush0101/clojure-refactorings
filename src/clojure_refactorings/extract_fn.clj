(ns clojure_refactorings.extract-fn)

(defn extract-fn
  [body]
  `(fn [] ~body))

(defn extract-defn
  [name body]
  (if-not (seq? body)
    (throw (Exception. "not a valid lisp form"))
  `(defn ~name [] ~body)))

(defn replace-with-fn-call
  [func body]
  `~func)

(defn extract-and-replace
  [name body]
  {:fn (extract-defn name body)
   :call `(~name)})
