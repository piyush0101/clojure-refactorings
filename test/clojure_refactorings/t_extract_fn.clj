(ns clojure_refactorings.t-extract-fn
  (:use midje.sweet)
  (:use [clojure_refactorings.extract-fn]))

(facts "about `extract-fn`"
       (fact "it extracts an anonymous function (fn) given a body with a single form"
             (extract-fn '(println "hello world")) => '(fn [] (println "hello world")))

       (fact "it extracts an anonymous function (fn) given a body as a nested sequence of more than one form"
             (extract-fn '(if (nil? sequence)
                                (print "nil")
                               (print "not nil"))) => '(fn [] (if (nil? sequence) (print "nil") (print "not nil"))))

       (fact "extracted function is a valid function that can be evaluated"
             ((eval (extract-fn '(+ 1 2)))) => 3))

(facts "about `extract-defn`"
       (fact "it extracts a function (defn) with a given name given a single form"
             (extract-defn 'display '((println "hello world"))) => '(defn display [] (println "hello world")))

       (fact "it extracts an named function (defn) given a body as a nested sequence of more than one form"
             (extract-defn 'conditional-print '((if (nil? sequence)
                                (print "nil")
                               (print "not nil")))) =>
             '(defn conditional-print [] (if (nil? sequence) (print "nil") (print "not nil"))))

       (fact "it extracts a named function (defn) given non nested lisp forms"
             (extract-defn 'add-and-subtract '((+ 1 2) (- 1 2))) => '(defn add-and-subtract [] (+ 1 2) (- 1 2)))
       )

(facts "about `extract-and-replace`"
       (fact "it throws an exception if not given a name of the function"
             (extract-and-replace-defn '(+ 1 2)) => (throws Exception))

       (fact "it throws an exception if not given a form"
             (extract-and-replace-defn 'display) => (throws Exception))

       (fact "it returns a map containing extracted function and replacement call"
             (extract-and-replace-defn 'display '((+ 1 2))) => {:fn '(defn display [] (+ 1 2)) :call '(display)}))

(facts "about `extract-defn-from-fn`"
       (fact "it converts a fn with no arguments to a defn with a given name"
             (extract-defn-from-fn 'add '(fn [] (+ 1 2))) => '(defn add [] (+ 1 2)))

       (fact "it converts a fn with one argument to a defn with a given name and a single argument"
             (extract-defn-from-fn 'display '(fn [data] (println data))) => '(defn display [data] (println data)))

       (fact "it converts a fn with multiple arguments to a defn with a given name and multiple arguments"
             (extract-defn-from-fn 'add '(fn [a b] (+ a b))) => '(defn add [a b] (+ a b))))

(facts "about `extract-and-replace-anonymous-fn`"
       (fact "it replaces anonymous fn with no arguments with function call"
             (:call (extract-and-replace-anonymous-fn 'add '(fn [] (+ 1 2)))) => '(add))

       (fact "it replaces anonymous fn with one argument with a function call"
             (:call (extract-and-replace-anonymous-fn 'display '(fn [data] (println data)))) => '(display data))

       (fact "it replaces anonymous fn with multiple arguments with a function call"
             (:call (extract-and-replace-anonymous-fn 'add '(fn [a b] (+ a b)))) => '(add a b)))

(facts "about `into-seq`"
       (fact "it converts two non nested forms into a seq with two forms"
             (into-seq "(+ 1 2) (- 1 2)") => '((+ 1 2) (- 1 2))))
