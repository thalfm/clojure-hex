(ns heroes-api.components.protocols
  (:require [schema.core :as s]))

(defprotocol IRepository
  (register [this]))

(def Repository (s/protocol IRepository))