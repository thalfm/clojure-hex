(ns heroes-api.components.service-map
  (:require [com.stuartsierra.component :as component]
            [heroes-api.ui.rest.hero-crud-controller :as rest]
            [heroes-api.ui.graphql.hero-crud-controller :as graphql]
            [io.pedestal.http :as http]
            [io.pedestal.http :as server]
            [io.pedestal.http.route :as route]))

(defn load-service-map
  ([environment]
   (load-service-map environment nil))
  ([environment query-handler]
   (let [routes (if query-handler
                  (clojure.set/union rest/endpoints
                                     (graphql/endpoints (:handler query-handler)))
                  rest/endpoints)]
     (-> {:env                 environment
          ::http/routes        (route/expand-routes routes)
          ::http/type          :jetty
          ::http/port          8890
          ::http/resource-path "/public"
          ::http/join?         false
          ::http/host          "0.0.0.0"}
         server/default-interceptors
         server/dev-interceptors))))

(defrecord ServiceMap
  [environment]

  component/Lifecycle
  (start [this]
    (let [query-handler (:query-handler this)]
      (assoc this :service-map (load-service-map environment query-handler))))

  (stop [this]
    (assoc this :service-map nil)))

(defn new-service-map
  [environment]
  (map->ServiceMap environment))


