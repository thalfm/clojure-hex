(ns heroes-api.base-test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer :all]
            [heroes-api.components :as components]
            [clojure.data.json :as json]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [schema.test]
            [heroes-api.ui.rest.hero-crud-controller :as rest])
  (:use [clojure.pprint]))

(use-fixtures :once schema.test/validate-schemas)

(def system (components/start! {:environment :test}))

(defn reset-database []
  (reset! (:conn (:database system)) []))

(defn- service-fn [system]
  (get-in system [:pedestal :service ::http/service-fn]))

(def service (service-fn system))

(def url-for
  (-> rest/endpoints
      route/expand-routes
      route/url-for-routes))

(defn request
  [verb keyword-path & {:keys [body path-params]}]
  (response-for service
                verb
                (url-for
                  keyword-path
                  :path-params path-params)
                :headers {"Content-Type" "application/json"}
                :body body))

(defn create-default-hero []
  (reset-database)
  (let [{:keys [status body]} (request
                                :post
                                :create-heroes
                                :body "{\n\"name\":\"Batman\",\n\"biography\":{\n\"full-name\":\"Bruce Wayne\",\n\"alias\":[\n\"The Dark Knight\"\n]\n},\n\"connections\":{\n\"group-affiliation\":[\n\"Justice League\"\n],\n\"relatives\": [\n\"Thomas Wayne\",\n\"Martha Wayne\"\n]\n}\n}")
        hero (:hero (json/read-json body))]
    {:status status :hero hero}))