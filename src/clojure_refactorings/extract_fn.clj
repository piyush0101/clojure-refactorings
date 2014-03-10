(ns clojure_refactorings.extract-fn)

(defn extract-fn
  [body]
  `(~(symbol "fn") [] ~body))

(defn extract-defn
  [name body]
  (if-not (seq? body)
    (throw (Exception. "not a valid lisp form"))
   (concat `(~(symbol "defn") ~name []) body)))

(defn extract-defn-from-fn
  [name body]
  (if-not (seq? body)
    (throw (Exception. "not a valid lisp form"))
  (cons 'defn (cons name (drop 1 body)))))

(defn extract-and-replace-defn
  [name body]
  {:fn (extract-defn name body)
   :call `(~name)})

(defn extract-and-replace-anonymous-fn
  [name body]
  {:fn (extract-defn-from-fn name body)
   :call (cons name (seq (nth body 1)))})

(defn into-seq
  [code-as-string]
  (read-string (str "("code-as-string")")))
