(ns heroes-api.infrastructure.persistence.in-memory.hero-repository
  (:require [heroes-api.core.domain.hero-repository :as hero-repository]))

(defmethod hero-repository/all! :in-memory
  [database _]
  @database)

(defmethod hero-repository/find-by-id! :in-memory
  [database id]
  (first (filter #(= (str (:uuid %)) id) @database)))

(defmethod hero-repository/create! :in-memory
  [database hero]
  (swap! database conj hero))

(defmethod hero-repository/update! :in-memory
  [database id hero]
  (let [database-filtered (filter #(not= (str (:uuid %)) id) @database)]
    (reset! database (conj database-filtered hero))))

(defmethod hero-repository/delete! :in-memory
  [database id]
  (let [database-filtered (filter #(not= (str (:uuid %)) id) @database)]
    (reset! database database-filtered)))