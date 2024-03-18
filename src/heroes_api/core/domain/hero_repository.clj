(ns heroes-api.core.domain.hero-repository
  (:import (clojure.lang Atom)
           [com.zaxxer.hikari HikariDataSource]))

(defn- select-db
  [db & _]
  (cond
    (instance? HikariDataSource db) :postgres
    (instance? Atom db) :in-memory
    :else :datomic))

(defmulti all! select-db)
(defmulti find-by-id! select-db)
(defmulti create! select-db)
(defmulti update! select-db)
(defmulti delete! select-db)
