(ns heroes-api.core.application.in
  (:require [schema.core :as s]))

(s/defschema HeroIn
             {:name              s/Str
              :full-name         s/Str
              :alias             [s/Str]
              :group-affiliation [s/Str]
              :relatives         [s/Str]})
