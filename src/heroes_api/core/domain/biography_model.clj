(ns heroes-api.core.domain.biography-model)

(defn new-biography
  ([full-name alias]
   {:full-name full-name :alias alias}))