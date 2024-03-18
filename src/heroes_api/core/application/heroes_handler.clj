(ns heroes-api.core.application.heroes-handler
  (:require [heroes-api.core.domain.hero-repository :as repo]
            [heroes-api.core.domain.hero-model :as model.hero]
            [heroes-api.core.domain.biography-model :as model.biography]))

(defn create!
  [store {:keys [name full-name alias group-affiliation relatives]}]
  (let [biography (model.biography/new-biography full-name alias)
        hero (model.hero/new-hero name biography group-affiliation relatives)]
    (repo/create! store hero)
    hero))

(defn show!
  [store uuid]
  (repo/find-by-id! store uuid))

(defn update!
  [store {:keys [uuid-hero name full-name alias group-affiliation relatives]}]
  (let [biography (model.biography/new-biography full-name alias)
        hero (model.hero/new-hero uuid-hero name biography group-affiliation relatives)]
    (repo/update! store uuid-hero hero)
    hero))

(defn destroy!
  [store uuid]
  (repo/delete! store uuid))

(defn index!
  [store]
  (repo/all! store {}))