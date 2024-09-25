(ns heroes-api.infrastructure.persistence.datomic.hero-repository
  (:require [datomic.api :as d]
            [heroes-api.core.domain.hero-repository :as hero-repository]
            [heroes-api.infrastructure.persistence.datomic.hero-schema :as schema]))

(defmethod hero-repository/all! :datomic
  [database, {:keys [id group-affiliation]}]
  (let [query {:query '{:find  [[(pull ?hero [*]) ...]]
                        :in    [$]
                        :where [[?hero :hero/uuid _]]}
               :args  [(d/db database)]}
        condition (cond-> query

                       id (->
                            (update-in [:query :in] conj '?id)
                            (update-in [:query :where] conj '[?hero :hero/uuid ?id])
                            (update-in [:args] conj id))
                       group-affiliation (->
                                           (update-in [:query :in] conj '?group-affiliation)
                                           (update-in [:query :where] conj '[?hero :connections/group-affiliation ?group-affiliation])
                                           (update-in [:args] conj group-affiliation))
                          )]
    (mapv schema/to->entity (d/query condition))))

(defmethod hero-repository/find-by-id! :datomic
  [database id]
  (let [heroes (hero-repository/all! database {:id (parse-uuid id)})]
    (if (not-empty heroes)
      (first heroes)
      {})))

(defmethod hero-repository/create! :datomic
  [database hero]
  (d/transact database [(schema/hero->schema hero)]))

(defmethod hero-repository/update! :datomic
  [database id hero]
  (hero-repository/create! database (assoc hero :uuid (parse-uuid id))))

(defmethod hero-repository/delete! :datomic
  [database id]
  (d/transact database [[:db/retractEntity [:hero/uuid (parse-uuid id)]]]))