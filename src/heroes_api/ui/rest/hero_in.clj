(ns heroes-api.ui.rest.hero-in)

(defn json-params->hero-in [json-params]
  (let [name (get-in json-params [:name])
        full-name (get-in json-params [:biography :full-name])
        alias (get-in json-params [:biography :alias])
        group-affiliation (get-in json-params [:connections :group-affiliation])
        relatives (get-in json-params [:connections :relatives])]
    {:name name
     :full-name full-name
     :alias alias
     :group-affiliation group-affiliation
     :relatives relatives}))
