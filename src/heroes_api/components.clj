(ns heroes-api.components
  (:require [com.stuartsierra.component :as component]
            #_[heroes-api.components.database-datomic :as datomic]
            [heroes-api.components.database-postgres :as postgres]
            [heroes-api.components.database-in-memory :as in-memory]
            [heroes-api.components.graphql-lacinia :as lacinia]
            [heroes-api.components.service-map :as service-map]
            [heroes-api.components.context-deps :as context-deps]
            [heroes-api.components.pedestal :as server]))

(defn base [configs]
  (component/system-map
    :database (postgres/new-database configs)
    :service-map (component/using (service-map/new-service-map configs) [:query-handler])
    :context-deps (component/using (context-deps/new-context-deps) [:database])
    :pedestal (component/using (server/new-pedestal) [:service-map :context-deps])
    :query-handler (component/using (lacinia/new-graphql-lucinia)
                                      [:database])))

(defn dev [configs]
  (merge (base configs)
         (component/system-map
           :database (in-memory/new-database))))

(def systems-map {:local   dev
                  :dev     dev
                  :test    dev
                  :base    base})

(defn ^:private system-for-env [environment systems]
  (get systems environment (:base systems)))

(defn create-and-start-system!
  ([] (create-and-start-system! {}))
  ([configs]
   (println "Starting system for environment: " (:environment configs))
   (let [system (system-for-env (:environment configs) systems-map)]
     (system configs))))

(defn start!
  ([] (start! {}))
  ([configs] (-> configs
                 create-and-start-system!
                 component/start)))