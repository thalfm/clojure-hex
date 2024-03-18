(ns heroes-api.ui.rest.hero-out)

(defn rest-hero-created [hero]
  {:status 201 :body {:message "Success created" :hero hero}})

(defn rest-hero-updated [hero]
  {:status 200 :body {:message "Success updated" :hero hero}})

(defn rest-hero-deleted []
  {:status 204 :body {:message "Success delete"}})

(defn rest-hero-found [hero]
  {:status 200 :body hero})

(defn rest-hero-list [heroes]
  {:status 200 :body heroes})