(ns heroes-api.infrastructure.persistence.postgres.adapter.hero
  (:require [heroes-api.core.domain.hero-model :as hero-model]))

(defn postgres-hero->model [hero]
  (let [uuid (:hero/id_hero hero)
        name (:hero/name hero)
        full-name (:biography/full_name hero)
        alias (into [] (:biography_aliases/aliases_name hero))
        relatives (into [] (:hero_relatives/relatives_name hero))
        group-affiliation (into [] (:hero_group_affiliation/group_affiliation_name hero))]
    (hero-model/new-hero uuid name {:full-name full-name :alias alias} group-affiliation relatives)))