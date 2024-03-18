(ns heroes-api.components.context-deps
  (:require [com.stuartsierra.component :as component]))

(defrecord ContextDeps
  []
  component/Lifecycle
  (start [this] this)
  (stop [this] this))

(defn new-context-deps
  []
  (map->ContextDeps {}))