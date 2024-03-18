(ns heroes-api.infrastructure.persistence.postgres.hero-repository
  (:require [heroes-api.core.domain.hero-repository :as hero-repository]
            [heroes-api.core.domain.hero-model :as hero-model]
            [heroes-api.infrastructure.persistence.postgres.adapter.hero :as postgres.adapter]
            [honey.sql :as sql]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [schema.core :as s]
            ))

(def insert-opts {:return-keys true :builder-fn rs/as-unqualified-lower-maps})

(defn- to-set [s]
  (if (set? s) s #{s}))

(defn- set-union [s1 s2]
  (clojure.set/union (to-set s1) (to-set s2)))

(defn- single-val [s]
  (if (and (set? s) (= 1 (count s)))
    (first s)
    s))

(defn- map-set->single-val-sanitized [m]
  (->> m
       (map (fn [[k v]] {k (single-val v)}))
       (into {})))

(defn- merge-equals-entity [entity1 entity2 comparator-keyword]
  (let [entity-sanitized (-> entity1 first map-set->single-val-sanitized)]
    (if (= (comparator-keyword entity-sanitized) (comparator-keyword entity2))
      (conj (drop 1 entity1) (merge-with set-union entity-sanitized entity2))
      (conj entity1 entity2))))

(defn merge-matches [property-map-list]
  (->> property-map-list
       (reduce #(merge-equals-entity %1 %2 :hero/id_hero) [])
       (map map-set->single-val-sanitized)))

(s/defmethod hero-repository/all! :postgres
  [database {:keys [id]}] :- [hero-model/Hero]
  (let [query     {:select    [:hero.id_hero
                               :hero.name
                               :biography.id_biography
                               :biography.full_name
                               :biography_aliases.aliases_name
                               :hero_relatives.relatives_name
                               :hero_group_affiliation.group_affiliation_name]
                   :from      [:hero]
                   :join      [:biography [:= :hero.id_biography :biography.id_biography]]
                   :left-join [:biography_aliases [:= :biography_aliases.id_biography :biography.id_biography]
                               :hero_relatives [:= :hero.id_hero :hero_relatives.id_hero]
                               :hero_group_affiliation [:= :hero.id_hero :hero_group_affiliation.id_hero]]}
        condition (cond-> []
                          id (conj [:= :hero.id_hero id]))
        q         (if condition (assoc query :where condition) query)
        result    (jdbc/execute!
                   database
                   (sql/format q))]
    (->> (merge-matches result)
         (map postgres.adapter/postgres-hero->model))))

(defmethod hero-repository/find-by-id! :postgres
  [database id] :- hero-model/Hero
  (-> (hero-repository/all! database {:id id})
      first))

(defmethod hero-repository/create! :postgres
  [database hero]
  (jdbc/with-transaction
   [tx database]
   (let [biography              (:biography hero)
         biography_created      (jdbc/execute-one!
                                 tx
                                 (sql/format {:insert-into [:biography]
                                              :columns     [:id_biography :full_name]
                                              :values      [[(random-uuid) (:full-name biography)]]})
                                 insert-opts)
         biography_aliases      (jdbc/execute!
                                 tx
                                 (sql/format {:insert-into [:biography_aliases]
                                              :columns     [:id_biography :aliases_name]
                                              :values      (mapv #(conj [(str (:id_biography biography_created))] %) (:alias biography))})
                                 insert-opts)
         hero_created           (jdbc/execute-one!
                                 tx
                                 (sql/format {:insert-into [:hero]
                                              :columns     [:id_hero :name :id_biography]
                                              :values      [[(random-uuid) (:name hero) (:id_biography biography_created)]]})
                                 insert-opts)
         hero_relatives         (jdbc/execute!
                                 tx
                                 (sql/format {:insert-into [:hero_relatives]
                                              :columns     [:id_hero :relatives_name]
                                              :values      (mapv #(conj [(:id_hero hero_created)] %) (:relatives hero))})
                                 insert-opts)
         hero_group_affiliation (jdbc/execute!
                                 tx
                                 (sql/format {:insert-into [:hero_group_affiliation]
                                              :columns     [:id_hero :group_affiliation_name]
                                              :values      (mapv #(conj [(:id_hero hero_created)] %) (:group-affiliation hero))})
                                 insert-opts)]
     (merge hero_created
            {:biography         {:full_name (:full_name biography_created)
                                 :aliases   (mapv :aliases_name biography_aliases)}
             :relatives         (mapv :relatives_name hero_relatives)
             :group-affiliation (mapv :group_affiliation_name hero_group_affiliation)}))))


(defmethod hero-repository/update! :postgres
  [database id hero]
  database)

(defmethod hero-repository/delete! :postgres
  [database id]
  database)