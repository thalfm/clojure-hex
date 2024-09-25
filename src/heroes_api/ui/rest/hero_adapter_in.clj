(ns heroes-api.ui.rest.hero-adapter-in
  (:require [schema.core :as s]
            [heroes-api.core.application.in :as in]))

(s/defschema JsonParams
  {:name        s/Str
   :biography   {:full-name s/Str
                 :alias     [s/Str]}
   :connections {:group-affiliation [s/Str]
                 :relatives         [s/Str]}})

(s/defn json-params->hero-in :- in/HeroIn
  [json-params :- JsonParams]
  (let [name              (get-in json-params [:name])
        full-name         (get-in json-params [:biography :full-name])
        alias             (get-in json-params [:biography :alias])
        group-affiliation (get-in json-params [:connections :group-affiliation])
        relatives (get-in json-params [:connections :relatives])]
    {:name name
     :full-name full-name
     :alias alias
     :group-affiliation group-affiliation
     :relatives relatives}))
