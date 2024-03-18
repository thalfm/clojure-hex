(ns heroes-api.components.database-datomic
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d]
            [config.core :refer [env]]
            [heroes-api.components.protocols :as protocols]
            [heroes-api.infrastructure.persistence.datomic.hero-schema :as schema-t]))

(defn register-repositories []
  (require '[heroes-api.infrastructure.persistence.datomic.hero-repository]))

(defrecord DatabaseDatomic
  [db-uri]
  protocols/RegisterRepositories

  (register [this]
    (register-repositories)
    this)

  component/Lifecycle

  (start [this]
    (d/create-database db-uri)
    (let [conn (d/connect db-uri)]
      (schema-t/create conn)
      (merge this {:conn conn
                   :uri  db-uri})))
  (stop [this]
    (if (:conn this) (d/release (:conn this)))
    (dissoc this :uri :conn)))

(defn new-database-datomic
  []
  (println "Start datomic connection")
  (let [store (->> (:datomic-secret-password env)
                   (str (:db-uri env))
                   ->DatabaseDatomic)]
    (protocols/register store)))

(defn new-database [environment]
  (new-database-datomic))