(ns heroes-api.infrastructure.message-bus.consumer)

(defn create!
  [{:keys [hero-in] {consumer :consumer} :context-deps}]
  (let [hero (in/horo->internal hero-in)]
    (application.heroes-handler/create! store hero)))

(def settings
  {:topics
   {:create! create!}})