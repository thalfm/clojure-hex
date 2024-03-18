(ns heroes-api.main
  (:require [heroes-api.components :as components])
  (:gen-class))

(defn -main
  [& _]
  (components/start!)
  (println "Starting server"))


