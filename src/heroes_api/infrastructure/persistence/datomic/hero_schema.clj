(ns heroes-api.infrastructure.persistence.datomic.hero-schema
  (:require [datomic.api :as d]))

(def ^:private hero-schema
  [{:db/ident       :hero/uuid
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "UUID of hero"}

   {:db/ident       :hero/name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "Name of hero"}

   {:db/ident       :hero/biography
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/isComponent true
    :db/doc         "Ref to biography"}

   {:db/ident       :hero/connections
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/isComponent true
    :db/doc         "Ref to connections"}

   ;biography
   {:db/ident       :biography/full-name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "Full name of hero"}

   {:db/ident       :biography/aliases
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/many
    :db/doc         "Aliases of hero"}

   ;connections
   {:db/ident       :connections/group-affiliation
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/many
    :db/doc         "Affinity groups to which the super hero is a part"}

   {:db/ident       :connections/relatives
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/many
    :db/doc         "Affinity groups to which the super hero is a part"}])

(defn create
  [conn]
  (d/transact conn hero-schema))

(defn biography->schema [{:keys [full-name alias]}]
  {:biography/full-name full-name
   :biography/aliases alias})

(defn connections->schema [group-affiliation relatives]
  {:connections/group-affiliation group-affiliation
   :connections/relatives relatives})

(defn hero->schema [{:keys [uuid name biography group-affiliation relatives]}]
  {:hero/uuid uuid
   :hero/name name
   :hero/biography (biography->schema biography)
   :hero/connections (connections->schema group-affiliation relatives)})

(defn biography->entity [schema]
  {:full-name (:biography/full-name schema)
   :aliases (:biography/aliases schema)})

(defn connections->entity [schema]
  {:group-affiliation (:connections/group-affiliation schema)
   :relatives (:connections/relatives schema)})

(defn to->entity [schema]
  (println schema)
  (when schema
    {:uuid        (str (:hero/uuid schema))
     :name        (:hero/name schema)
     :biography   (biography->entity (:hero/biography schema))
     :connections (connections->entity (:hero/connections schema))}))