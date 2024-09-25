(ns heroes-api.components.database-in-memory
  (:require [com.stuartsierra.component :as component]
            [heroes-api.components.protocols :as protocols]))

(defn register-repositories []
  (require '[heroes-api.infrastructure.persistence.in-memory.hero-repository]))

(defrecord DatabaseInMemory []
  protocols/IRepository
  (register [this]
    (register-repositories)
    this)

  component/Lifecycle
  (start [this]
    (assoc this :conn (atom [])))

  (stop [this]
    (assoc this :conn nil)))

(defn new-database-in-memory []
  (println "Start In Memory connection")
  (let [store (->DatabaseInMemory)]
    (protocols/register store)))

(defn new-database []
  (new-database-in-memory))