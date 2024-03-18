(ns heroes-api.ui.graphql.hero-out)

(defn json-response [status body]
  {:status  status
   :body    body})

(defn hero-found [hero]
  (let [hero-result (clojure.walk/postwalk-replace {:id       :uuid
                                                    :fullName :full-name} (:data hero))]
    (-> hero-result first second)))

(defn adapt [result]
  (if (-> result :errors seq)
    (json-response 400 result)
    (let [out-type (ffirst (:data result))]
      (case out-type
        :heroById (->> result hero-found (json-response 200))))))
