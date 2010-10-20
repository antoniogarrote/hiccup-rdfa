(defproject hiccup-rdfa "1.0.0-SNAPSHOT"
  :description "Utility functions to generate XHTML+RDFa documents in using hiccup"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [hiccup "0.2.7"]
                 [com.hp.hpl.jena/jena "2.6.2"]
                 [com.hp.hpl.jena/arq "2.8.3"]
                 [net.rootdev/java-rdfa "0.3"]]
  :dev-dependencies [[leiningen/lein-swank "1.1.0"]])
