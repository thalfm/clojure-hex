(ns heroes-api.components.graphql-lacinia
  (:require [clojure.data.json :as json]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [com.stuartsierra.component :as component]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia :refer [execute]]
            [heroes-api.ui.graphql.hero-crud-controller :as hero-crud-controller]))

(defn load-schema
  [component]
  (-> (io/resource "heroes-ql-schema.edn")
      slurp
      edn/read-string
      (util/inject-resolvers (hero-crud-controller/resolver-map component))
      schema/compile))

(defn variable-map
  "Reads the `variables` query parameter, which contains a JSON string
  for any and all GraphQL variables to be associated with this request.

  Returns a map of the variables (using keyword keys)."
  [request]
  (let [vars (get-in request [:params :variables])]
    (if-not (str/blank? vars)
      (json/read-str vars :key-fn keyword)
      {})))

(defn extract-query
  [request]
  (case (:request-method request)
    :get (get-in request [:params :query])
    :post (slurp (:body request))
    :else ""))

(defn ^:private graphql-handler
  "Accepts a GraphQL query via GET or POST, and executes the query.
  Returns the result as text/json."
  [compiled-schema]
  (let [context {:cache (atom {})}]
    (fn [request]
      (let [vars   (variable-map request)
            query  (extract-query request)]
        (execute compiled-schema query vars context)))))

(defrecord QueryHandlerProvider [schema]

  component/Lifecycle
  (start [this]
    (let [compiled-schema (load-schema this)]
      (assoc this :handler (graphql-handler compiled-schema))))

  (stop [this]
    (assoc this :handler nil)))

(defn new-graphql-lucinia []
  (map->QueryHandlerProvider {}))