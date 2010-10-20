(ns hiccup-rdfa.core
  (:import (com.hp.hpl.jena.rdf.model ModelFactory)
           (com.hp.hpl.jena.query QueryExecutionFactory)))

(def *rdf-ns* (ref {}))
(def *rdfa-fn-table* (ref {}))

(defn add-ns
  ([ns uri]
     (dosync (alter *rdf-ns* (fn [m] (assoc m ns uri))))))

(defn make-vocabulary
  "Defines a new RDFS vocabulary"
  ([map-definition]
     (make-vocabulary (:prefix map-definition)
                      (:namespace map-definition)
                      (:classes map-definition)
                      (:properties map-definition)))
  ([prefix-uri namespace classes properties]
     { :prefix     prefix-uri
       :namespace  namespace
       :classes    classes
       :properties properties }))

(defn- attribute-hash
  ([definition-hash]
     (let [attributes-pairs (filter (fn [[k v]] (and (not= k :tag)
                                                     (not (nil? v))))
                                    definition-hash)]
       (reduce (fn [h [k v]] (assoc h k v)) {} attributes-pairs))))

(defn- complete-ns
  ([xhtml-attrs]
     (reduce (fn [h [p uri]] (assoc h (str "xmlns:" p) uri)) xhtml-attrs @*rdf-ns*)))

(defn xhtml-rdfa-tag
  "Create an XHTML+RDFa tag for the specified language."
  [lang & contents]
  (vec (concat
        [:html (complete-ns
                {:xmlns "http://www.w3.org/1999/xhtml"
                 "xml:lang" lang
                 :version "XHTML+RDFa 1.0"})]
        contents)))

(defn rdfa-tag
  "Generates a RDFa based tag"
  ([options & body]
     (let [options-with-defaults (-> options
                                     (assoc :tag (or (:tag options) :span)))
           tag-attributes [(:tag options-with-defaults)
                           (attribute-hash options-with-defaults)]]
       (vec (if (or (empty? body)
                    (nil? (first body)))
              tag-attributes
              (concat tag-attributes body))))))

(defn def-rdfa-property
  "Defines a RDFa property function"
  ([property]
     (let [fn-name  (.replace property ":" "-")
           rdfa-fn (fn [& props-body]
                     (let [props (assoc  (or (first (filter #(map? %) props-body)) {}) :property property)
                           body  (filter #(not (map? %)) props-body)
                           is-body-uri (and (string? (first body))
                                            (or (= 0 (.indexOf (first body) "http"))
                                                (= 0 (.indexOf (first body) "#"))))]
                       (if is-body-uri (apply rdfa-tag (concat [(-> props
                                                                    (assoc :resource (first body)))]
                                                               (if (empty? (rest body)) nil (rest body))))
                           (apply rdfa-tag (concat [props] body)))))
           rdfa-link-fn (fn [uri & props]
                          (apply rdfa-tag [(-> (if (map? (first props)) (first props) {})
                                               (assoc :tag (if (:tag (first props)) (:tag (first props)) :link))
                                               (assoc :href uri)
                                               (assoc :property property))
                                           (first (filter #(not (map? %)) props))]))]
       (dosync (alter *rdfa-fn-table* (fn [m] (assoc m fn-name {:prop rdfa-fn :link rdfa-link-fn}))))
       (eval (read-string (str "(defn " fn-name " [& props-body] (apply (:prop (get @hiccup-rdfa.core/*rdfa-fn-table* \"" fn-name "\")) props-body))")))
       (eval (read-string (str "(defn link-" fn-name "-to [uri & props] (apply (:link (get @hiccup-rdfa.core/*rdfa-fn-table* \"" fn-name "\")) (concat [uri] props)))"))))))

(defn def-rdfa-instance
  "Defines a RDFa instance function"
  ([class]
     (let [fn-name  (.replace class ":" "-")
           rdfa-fn (fn [& uri-props-body]
                     (let [uri   (first (filter #(and (string? %)
                                                 (or (= 0 (.indexOf % "http"))
                                                     (= 0 (.indexOf % "#")))) uri-props-body ))
                           props (assoc  (or (first (filter #(map? %) uri-props-body)) {}) :about uri)
                           body  (filter #(and (not= uri %) (not (map? %))) uri-props-body)]
                       (apply rdfa-tag (concat [(assoc props :typeof class)] body))))]
       (dosync (alter *rdfa-fn-table* (fn [m] (assoc m fn-name rdfa-fn))))
       (eval (read-string (str "(defn " fn-name " [ & uri-props-body] (apply (get @hiccup-rdfa.core/*rdfa-fn-table* \"" fn-name "\") uri-props-body))"))))))


(defn register-vocabulary
  "Registers a RDFa vocabulary, generating helper methods for
   the defined classes and properties"
  ([vocabulary]
     (add-ns (:namespace vocabulary) (:prefix vocabulary))
     (doseq [class (:classes vocabulary)]
       (let [curie (str (:namespace vocabulary) ":" class)]
         (def-rdfa-instance curie)))
     (doseq [property (:properties vocabulary)]
       (let [curie (str (:namespace vocabulary) ":" property)]
         (def-rdfa-property curie)))))



(defn- parse-format
  ([format]
     (cond (= (.toLowerCase (name format)) "xml") "RDF/XML"
           (= (.toLowerCase (name format)) "ntriple") "N-TRIPLE"
           (= (.toLowerCase (name format)) "n3") "N3"
           (= (.toLowerCase (name format)) "ttl") "TURTLE"
           (= (.toLowerCase (name format)) "turtle") "TTL"
           (= (.toLowerCase (name format)) "xhtml") "XHTML"
           (= (.toLowerCase (name format)) "html") "HTML"
           (= (.toLowerCase (name format)) "trig") "TRIG"
           (= (.toLowerCase (name format)) "trix") "TRIX"
           true "RDF/XML")))


(defn- make-model
  "Creates a JENA model"
  ([] (ModelFactory/createDefaultModel)))


(def *property-uris* ["<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>"
                      "<http://www.w3.org/2002/07/owl#DatatypeProperty>"
                      "<http://www.w3.org/2002/07/owl#ObjectProperty>"])

(def *class-uris* ["<http://www.w3.org/2000/01/rdf-schema#Class>"
                   "<http://www.w3.org/2002/07/owl#Class>"])

(defn- load-stream [model stream format]
  (let [format (parse-format format)]
    (.read model stream format)))

(defn- query-model
  ([model query-string]
     (let [qexec (QueryExecutionFactory/create query-string model)
           results (iterator-seq (.execSelect qexec))]
       results)))

(defn- grab-document-url
  "Retrieves an input stream from a remote URL"
  ([url]
     (let [url (java.net.URL. url)
           conn (.openConnection url)]
       (.getInputStream conn))))

(defn- process-model-query-result
  "Transforms a query result into a dicitionary of bindings"
  ([result]
     (let [vars (iterator-seq (.varNames result))]
       (reduce (fn [acum item] (assoc acum (keyword (str "?" item)) (.get result item))) {} vars))))


(defn- get-types
  ([types model]
     (let [results (reduce (fn [ac prop] (concat ac (query-model model (str "SELECT ?s { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> " prop " }"))))
                           []
                           types)]
       (distinct (map #(str (get (process-model-query-result %) :?s)) results)))))


(defn make-vocabulary-from-stream
  "Generates a new vocabulary description hash with the ontology retrievable at the provided stream.
   Attrs:
    - stream     : Stream containing the vocabulary
    - mime       : MIME type of the ontology as a symbol [:xml, :n3, :turtle...]
    - namespace  : namespace used as an alias for the namespace uri prefix [foaf, rdf, sioc...]
    - uri-prefix : URI fragment that will be substitued by the prefix [http://www.w3.org/1999/02/22-rdf-syntax-ns#]"
  ([stream mime uri-prefix namespace]
     (let [model (make-model)]
       (load-stream model stream mime)
       (let [props (get-types *property-uris* model)
             classes (get-types *class-uris* model)
             selected-props (map (fn [uri] (str (nth (vec (.split uri uri-prefix)) 1)))
                                 (filter (fn [uri] (= 0 (.indexOf uri uri-prefix))) props))
             selected-classes (map (fn [uri] (str (nth (vec (.split uri uri-prefix)) 1)))
                                   (filter (fn [uri] (= 0 (.indexOf uri uri-prefix))) classes))]
         (make-vocabulary {:prefix uri-prefix
                           :namespace namespace
                           :classes selected-classes
                           :properties selected-props})))))

(defn make-vocabulary-from-url
  "Generates a new vocabulary description hash with the ontology retrievable at the provided namespace.
   Attrs:
    - url        : URL of the document containing the vocabulary
    - mime       : MIME type of the ontology as a symbol [:xml, :n3, :turtle...]
    - namespace  : namespace used as an alias for the namespace uri prefix [foaf, rdf, sioc...]
    - uri-prefix : URI fragment that will be substitued by the prefix [http://www.w3.org/1999/02/22-rdf-syntax-ns#]"
  ([url mime uri-prefix namespace]
     (make-vocabulary-from-stream (grab-document-url url) mime uri-prefix namespace)))
