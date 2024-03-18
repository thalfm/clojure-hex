(ns heroes-api.ui.rest.hero-crud-controller
  (:require [heroes-api.core.application.heroes-handler :as application.heroes-handler]
            [heroes-api.ui.rest.hero-in :as in]
            [heroes-api.ui.rest.hero-out :as out]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]))

(defn create!
  [{:keys [json-params] {{store :conn} :database} :context-deps}]
  (let [hero-in (in/json-params->hero-in json-params)
        hero-created (application.heroes-handler/create! store hero-in)]
    (out/rest-hero-created hero-created)))

(defn show!
  [{:keys [path-params] {{store :conn} :database} :context-deps}]
  (let [hero (application.heroes-handler/show! store (:id path-params))]
    (out/rest-hero-found hero)))

(defn update!
  [{:keys [path-params json-params] {{store :conn} :database} :context-deps}]
  (let [uuid-hero (:id path-params)
        hero-in (assoc (in/json-params->hero-in json-params) :uuid-hero uuid-hero)
        hero-updated (application.heroes-handler/update! store hero-in)]
    (out/rest-hero-updated hero-updated)))

(defn destroy!
  [{:keys [path-params] {{store :conn} :database} :context-deps}]
  (let [uuid (:id path-params)]
    (application.heroes-handler/destroy! store uuid)
    (out/rest-hero-deleted)))

(defn index!
  [{{{store :conn} :database} :context-deps}]
  (let [heroes (application.heroes-handler/index! store)]
    (out/rest-hero-list heroes)))

(def endpoints
  #{["/heroes" :post [(body-params/body-params) create!] :route-name :create-heroes]
    ["/heroes/:id" :get show! :route-name :read-hero]
    ["/heroes/:id" :put [(body-params/body-params) update!] :route-name :update-heroes]
    ["/heroes/:id" :delete destroy! :route-name :delete-heroes]
    ["/heroes" :get index! :route-name :list-heroes]})