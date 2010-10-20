(ns hiccup-rdfa.vocabularies)

(def rdf-vocabulary
     {:prefix "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
      :namespace "rdf"
      :classes '("List" "Alt" "Seq" "Bag" "Statement" "Property")
      :properties '("rest" "first" "value" "object" "predicate" "subject" "type")})

(def rdfs-vocabulary
     {:prefix "http://www.w3.org/2000/01/rdf-schema#"
      :namespace "rdfs"
      :classes '("Resource" "ContainerMembershipProperty" "Datatype" "Class" "Container" "Literal")
      :properties '("subPropertyOf" "isDefinedBy" "range" "subClassOf" "seeAlso" "member" "comment" "domain" "label")})

(def owl-vocabulary
     {:prefix "http://www.w3.org/2002/07/owl#"
      :namespace "owl"
      :classes '("AllDifferent" "AnnotationProperty" "ReflexiveProperty" "AsymmetricProperty" "IrreflexiveProperty" "Ontology" "InverseFunctionalProperty" "Axiom" "DeprecatedClass" "Class" "NegativePropertyAssertion" "FunctionalProperty" "Restriction" "TransitiveProperty" "DataRange" "SymmetricProperty" "AllDisjointProperties" "ObjectProperty" "NamedIndividual" "DeprecatedProperty" "Annotation" "DatatypeProperty" "AllDisjointClasses" "OntologyProperty" "Thing" "Nothing")
      :properties '("equivalentProperty" "assertionProperty" "onProperties" "onProperty" "complementOf" "equivalentClass" "cardinality" "onDatatype" "unionOf" "minQualifiedCardinality" "disjointUnionOf" "targetIndividual" "maxCardinality" "sameAs" "withRestrictions" "differentFrom" "hasSelf" "sourceIndividual" "someValuesFrom" "disjointWith" "oneOf" "propertyChainAxiom" "allValuesFrom" "targetValue" "members" "qualifiedCardinality" "maxQualifiedCardinality" "propertyDisjointWith" "annotatedProperty" "onDataRange" "onClass" "annotatedSource" "inverseOf" "distinctMembers" "hasKey" "hasValue" "minCardinality" "annotatedTarget" "intersectionOf" "datatypeComplementOf" "topDataProperty" "bottomDataProperty" "bottomObjectProperty" "topObjectProperty")})


(def sioc-vocabulary
     {:prefix "http://rdfs.org/sioc/ns#"
      :namespace "sioc"
      :classes '("Thread" "UserAccount" "Usergroup" "Post" "Item" "Space" "Community" "Site" "Container" "Role" "Forum")
      :properties '("ip_address" "modified_at" "content_encoded" "num_items" "last_name" "num_threads" "last_item_date" "description" "first_name" "num_replies" "email_sha1" "id" "title" "last_reply_date" "note" "subject" "last_activity_date" "num_views" "created_at" "name" "content" "num_authors" "function_of" "owner_of" "has_container" "about" "latest_version" "has_scope" "has_part" "has_administrator" "has_group" "parent_of" "has_modifier" "group_of" "subscriber_of" "has_usergroup" "links_to" "email" "has_discussion" "feed" "part_of" "has_moderator" "administrator_of" "moderator_of" "container_of" "has_member" "host_of" "member_of" "has_space" "reply_of" "previous_by_date" "modifier_of" "account_of" "space_of" "next_by_date" "scope_of" "creator_of" "addressed_to" "follows" "has_parent" "attachment" "topic" "usergroup_of" "link" "has_creator" "previous_version" "has_host" "has_reply" "has_owner" "has_function" "reference" "next_version" "has_subscriber" "avatar" "related_to" "embeds_knowledge")})

(def foaf
     {:prefix "http://xmlns.com/foaf/0.1/"
      :namespace "foaf"
      :classes '("Project" "Organization" "OnlineAccount" "Document" "Agent" "Image" "Person" "Group" "OnlineGamingAccount" "LabelProperty" "OnlineChatAccount" "PersonalProfileDocument" "OnlineEcommerceAccount")
      :properties '("mbox" "dnaChecksum" "accountName" "made" "familyName" "primaryTopic" "gender" "knows" "title" "thumbnail" "surname" "topic" "givenname" "yahooChatID" "member" "family_name" "maker" "openid" "topic_interest" "nick" "geekcode" "fundedBy" "phone" "plan" "schoolHomepage" "aimChatID" "msnChatID" "skypeID" "page" "lastName" "isPrimaryTopicOf" "homepage" "membershipClass" "accountServiceHomepage" "mbox_sha1sum" "weblog" "pastProject" "focus" "name" "sha1" "depicts" "firstName" "depiction" "jabberID" "theme" "myersBriggs" "icqChatID" "tipjar" "logo" "based_near" "givenName" "currentProject" "birthday" "status" "publications" "interest" "workInfoHomepage" "account" "age" "img" "workplaceHomepage" "holdsAccount")})
