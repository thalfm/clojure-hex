(ns heroes-api.infrastructure.persistence.in-memory.hero-repository
  (:require [heroes-api.core.domain.hero-repository :as hero-repository]
            [heroes-api.core.domain.hero-model :as hero-model]
            [schema.core :as s]))

(defmethod hero-repository/all! :in-memory
  [database _]
  @database)

(defmethod hero-repository/find-by-id! :in-memory
  [database id]
  (first (filter #(= (str (:uuid %)) id) @database)))

(s/defmethod hero-repository/create! :in-memory
  [database :- s/atom
   hero :- hero-model/Hero]
  (swap! database conj hero))

(defmethod hero-repository/update! :in-memory
  [database id hero]
  (let [database-filtered (filter #(not= (str (:uuid %)) id) @database)]
    (reset! database (conj database-filtered hero))))

(defmethod hero-repository/delete! :in-memory
  [database id]
  (let [database-filtered (filter #(not= (str (:uuid %)) id) @database)]
    (reset! database database-filtered)))