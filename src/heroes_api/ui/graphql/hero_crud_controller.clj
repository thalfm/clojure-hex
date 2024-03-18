(ns heroes-api.ui.graphql.hero-crud-controller
  (:require [heroes-api.ui.graphql.hero-out :as hero-out]
            [heroes-api.core.application.heroes-handler :as application.heroes-handler]))


(defn show!
  [store]
  (fn [_ args _]
    (let [data (application.heroes-handler/show! store (:id args))]
      (clojure.walk/postwalk-replace {:uuid      :id
                                      :full-name :fullName} data))))


(defn resolver-map
  [{{store :conn} :database}]
  {:Query/heroById (show! store)})

(defn endpoints
  [query-handler]
  #{["/graphql" :post query-handler :route-name :graphql-post]
    ["/graphql" :get #(-> % query-handler hero-out/adapt) :route-name :graphql-get]})