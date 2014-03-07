(ns clojure_refactorings.t-extract-fn
  (:use midje.sweet)
  (:use [clojure_refactorings.extract-fn]))

(facts "about `extract-fn`"
       (fact "it extracts an anonymous function (fn) given a body with a single form"
             (extract-fn '(println "hello world")) => '(clojure.core/fn [] (println "hello world")))

       (fact "it extracts an anonymous function (fn) given a body as a nested sequence of more than one form"
             (extract-fn '(if (nil? sequence)
                                (print "nil")
                               (print "not nil"))) => '(clojure.core/fn [] (if (nil? sequence) (print "nil") (print "not nil"))))

       (fact "extracted function is a valid function that can be evaluated"
             ((eval (extract-fn '(+ 1 2)))) => 3))

(facts "about `extract-defn`"
       (fact "it extracts a function (defn) with a given name given a single form"
             (extract-defn 'display '(println "hello world")) => '(clojure.core/defn display [] (println "hello world")))

       (fact "it extracts an named function (defn) given a body as a nested sequence of more than one form"
             (extract-defn 'conditional-print '(if (nil? sequence)
                                (print "nil")
                               (print "not nil"))) =>
             '(clojure.core/defn conditional-print [] (if (nil? sequence) (print "nil") (print "not nil"))))

       (fact "it throws an exception when given an invalid lisp form"
             (extract-defn 'display [+ 1 2]) => (throws Exception)))

(facts "about `replace-with-fn-call`"
       (fact "it replaces a body with a named function call with no arguments"
             (replace-with-fn-call '(display) '(println "hello world")) => '(display)))

(facts "about `extract-and-replace`"
       (fact "it throws an exception if not given a name of the function"
             (extract-and-replace '(+ 1 2)) => (throws Exception))

       (fact "it throws an exception if not given a form"
             (extract-and-replace 'display) => (throws Exception))

       (fact "it returns a map containing extracted function and replacement call"
             (extract-and-replace 'display '(+ 1 2)) => {:fn '(clojure.core/defn display [] (+ 1 2)) :call '(display)}))
