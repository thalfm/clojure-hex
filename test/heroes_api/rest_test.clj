(ns heroes-api.rest-test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer :all]
            [heroes-api.base-test :as test]
            [clojure.data.json :as json]))

(def batman {:name        "Batman"
             :biography   {:full-name "Bruce Wayne"
                           :alias     ["The Dark Knight"]}
             :group-affiliation ["Justice League"]
             :relatives         ["Thomas Wayne"
                                 "Martha Wayne"] })

(defn- hero-without-uuid
  [hero]
  (-> hero
      (update-in [:hero] dissoc :uuid)
      (update-in [:hero :biography] dissoc :uuid)))

(deftest create-hero-test
  (is (= (hero-without-uuid (test/create-default-hero))
         {:status 201
          :hero   batman})))

(deftest read-hero-test
  (let [{{:keys [uuid]} :hero} (test/create-default-hero)
        {:keys [status body]} (test/request :get :read-hero :path-params {:id uuid})
        hero (json/read-json body)
        result {:status status :hero hero}]
    (is (= (hero-without-uuid result) {:hero   batman
                                       :status 200}))))


(deftest update-hero-test
  (let [{{:keys [uuid]} :hero} (test/create-default-hero)
        {:keys [status body]} (test/request
                                :put
                                :update-heroes
                                :path-params {:id uuid}
                                :body "{\n\"name\":\"Batman II\",\n\"biography\":{\n\"full-name\":\"Bruce Wayne\",\n\"alias\":[\n\"The Dark Knight\"\n]\n},\n\"connections\":{\n\"group-affiliation\":[\n\"Justice League\"\n],\n\"relatives\": [\n\"Thomas Wayne\",\n\"Martha Wayne\"\n]\n}\n}")
        body->map (json/read-json body)
        result {:status status :hero (:hero body->map)}]
    (is (= (hero-without-uuid result) {:hero   {:biography {:alias      ["The Dark Knight"]
                                                            :full-name  "Bruce Wayne"}
                                                :group-affiliation ["Justice League"]
                                                :relatives         ["Thomas Wayne"
                                                                    "Martha Wayne"]
                                                :name      "Batman II"}
                                       :status 200}))))


(deftest delete-hero-test
  (let [{{:keys [uuid]} :hero} (test/create-default-hero)
        _ (test/request :delete :delete-heroes :path-params {:id uuid})
        {:keys [status body]} (test/request :get :read-hero :path-params {:id uuid})
        body->map (json/read-json body)
        result {:status status :hero (:hero body->map)}]
    (is (= result {:status 200 :hero nil}))))


(deftest list-heroes-test
  (test/reset-database)
  (let [{:keys [hero]} (test/create-default-hero)
        {:keys [status body]} (test/request :get :list-heroes)
        heros (json/read-json body)
        result {:status status :heroes heros}]
    (is (= result {:heroes [{:biography {:alias      ["The Dark Knight"]
                                         :full-name  "Bruce Wayne"}
                             :name      "Batman"
                             :group-affiliation ["Justice League"]
                             :relatives         ["Thomas Wayne"
                                                 "Martha Wayne"]
                             :uuid      (:uuid hero)}]
                   :status 200}))))