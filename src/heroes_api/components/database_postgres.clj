(ns heroes-api.components.database-postgres
  (:require [com.stuartsierra.component :as component]
            [config.core :refer [env]]
            [heroes-api.components.protocols :as protocols]
            [next.jdbc.connection :as connection])
  (:import [com.zaxxer.hikari HikariDataSource]
           [java.io Closeable]))

(defn register-repositories []
  (require '[heroes-api.infrastructure.persistence.postgres.hero-repository]))

(defrecord DatabasePostgres
  [config]
  protocols/IRepository

  (register [this]
    (register-repositories)
    this)

  component/Lifecycle

  (start [this]
    (let [ds (connection/->pool HikariDataSource config)]
      (println "Start postgres connection")
      (merge this {:conn ds})))
  (stop [this]
    (when (isa? (:conn this) Closeable)
      (do
        (println "Close postgres connection")
        (.close (:conn this))))))

(defn new-database
  [configs]
  (let [user            (:postgres-user env)
        password        (:postgres-password env)
        postgres-config (-> (:postgres env)
                            (assoc :username user)
                            (assoc :password password))
        store           (->DatabasePostgres (merge postgres-config configs))]
    (protocols/register store)))

(defn conj-same-map
  [m1 m2 keyword-id keyword-merge]
  (if (= (keyword-id m1) (keyword-id m2))
    (assoc m1 keyword-merge (-> m1
                                keyword-merge
                                vector
                                (conj (keyword-merge m2))))))