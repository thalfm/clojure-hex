(ns heroes-api.infrastructure.message-bus.producer)

(defn create!
  [{:keys [json-params] {producer :producer} :context-deps}]
  (let [hero-in (in/json-params->hero-out json-params)]
        (p.produce/produce! producer {:topic :create :message hero-in})))