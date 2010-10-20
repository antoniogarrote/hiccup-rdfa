(ns hiccup-rdfa.test.core
  (:use [hiccup-rdfa.core] :reload)
  (:use [clojure.test]))

(def *vocab* (make-vocabulary "http://www.w3.org/2000/01/rdf-schema#"
                              "rdfs"
                              ["Container" "Literal"]
                              ["isDefinedBy"]))
(register-vocabulary *vocab*)

(deftest def-vocabulary-test
  (is (= "rdfs" (:namespace *vocab*)))
  (is (= "http://www.w3.org/2000/01/rdf-schema#" (:prefix *vocab*)))
  (is (= ["Container" "Literal"] (:classes *vocab*)))
  (is (= ["isDefinedBy"] (:properties *vocab*))))


(deftest rdfa-tag-without-body-test
  (is (= [:span {:property "rdfs:type"}] (rdfa-tag {:property "rdfs:type"}))))

(deftest rdfap-tag-with-body-test
  (is (= [:span {:property "rdfs:type"} [:a {:href "http://test.com"} "test"]]
         (rdfa-tag {:property "rdfs:type"} [:a {:href "http://test.com"} "test"]))))

(deftest register-vocabulary-instances
  (is (= [:span {:about "http://test.com/test1", :typeof "rdfs:Container"}]
           (rdfs-Container "http://test.com/test1")))
  (is (= [:p {:id "test1", :about "http://test.com/test1", :typeof "rdfs:Container"}]
           (rdfs-Container "http://test.com/test1" {:id "test1" :tag :p})))
  (is (= [:span {:typeof "rdfs:Container"}]
           (rdfs-Container)))
  (is (= [:p {:id "test1", :typeof "rdfs:Container"}]
           (rdfs-Container {:id "test1" :tag :p})))
  (is (= [:p {:id "test1", :about "http://test.com/test1", :typeof "rdfs:Container"} [:p {:id "test"} "great"]]
           (rdfs-Container "http://test.com/test1" {:id "test1" :tag :p} [:p {:id "test"} "great"]))))

(deftest register-vocabulary-properties
  (is (= [:span {:property "rdfs:isDefinedBy"} "value"]
           (rdfs-isDefinedBy "value")))
  (is (= ["p" {:id "test", :property "rdfs:isDefinedBy"} "value"]
           (rdfs-isDefinedBy "value" {:id "test" :tag "p"})))
  (is (= ["p" {:id "test", :property "rdfs:isDefinedBy"} "value"]
           (rdfs-isDefinedBy {:id "test" :tag "p"} "value")))
  (is (= ["p" {:id "test", :property "rdfs:isDefinedBy"} "value" [:p "hey"]]
           (rdfs-isDefinedBy "value" {:id "test" :tag "p"} [:p "hey"]))))

(deftest xhtmla-declaration
  (is (= [:html {"xmlns:rdfs" "http://www.w3.org/2000/01/rdf-schema#"
                 :xmlns "http://www.w3.org/1999/xhtml"
                 "xml:lang" :en
                 :version "XHTML+RDFa 1.0"}
          [:p "inside"]]
       (xhtml-rdfa-tag :en [:p "inside"]))))
