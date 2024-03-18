(ns heroes-api.core.domain.hero-model
  (:require [schema.core :as s]))

(s/defschema Relatives [s/Str])

(s/defschema GroupAffiliation [s/Str])

(s/defschema Biography
  {:full-name s/Str
   :alias     [s/Str]})

(s/defschema Uuid (s/cond-pre s/Uuid s/Str))

(s/defschema Hero {:uuid              Uuid
                   :name              s/Str
                   :biography         Biography
                   :group-affiliation GroupAffiliation
                   :relatives         Relatives})

(s/defn ^:always-validate new-hero :- Hero
  ([name :- s/Str
    biography :- Biography
    group-affiliation :- GroupAffiliation
    relatives :- Relatives]
   {:uuid (random-uuid)
    :name name
    :biography biography
    :group-affiliation group-affiliation
    :relatives relatives})

  ([uuid :- Uuid
    name :- s/Str
    biography :- Biography
    group-affiliation :- GroupAffiliation
    relatives :- Relatives]
   {:uuid uuid
    :name name
    :biography biography
    :group-affiliation group-affiliation
    :relatives relatives}))

