(ns heroes-api.components.pedestal
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http :as http]
            [io.pedestal.interceptor :as i]
            [clojure.data.json :as json]))

(defn- test?
  [service-map]
  (= :test (:env service-map)))

(defn- context-deps-in-request-before
  "Associates context dependencies in the Pedestal context map."
  [context-deps]
  (i/interceptor
    {:name  ::insert-context
     :enter (fn [context]
              (assoc-in context [:request :context-deps] context-deps))}))

(defn- add-pedestal-before-interceptor
  "Adds an interceptor of enter to the pedestal which associates the
  component into the Pedestal context map."
  [service-map context-deps]
  (update-in service-map [::http/interceptors]
             #(vec (->> % (cons (context-deps-in-request-before context-deps))))))

(defn- parse-json
  [response]
  (-> response
      (update :body json/write-str)
      (assoc-in [:headers "Content-Type"] "Application/json")))

(defn- to->json []
  (let [response-json {:name  ::response-json
                       :leave #(update-in % [:response] parse-json)}]
    (i/interceptor response-json)))

(defrecord Pedestal [service-map service context-deps]
  component/Lifecycle

  (start [this]
    (if service
      this
      (do
        (cond-> (:service-map service-map)
                true (add-pedestal-before-interceptor context-deps)
                true (update ::http/interceptors conj (to->json))
                true http/create-server
                (not (test? (:service-map service-map))) http/start
                true ((partial assoc this :service))))))

  (stop [this]
    (when (and service (not (test? (:service-map service-map))))
      (http/stop service))
    (assoc this :service nil)))

(defn new-pedestal []
  (map->Pedestal {}))