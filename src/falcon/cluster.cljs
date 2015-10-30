(ns falcon.cluster
  (:require 
    [cljs.core.async :as async]
    [schema.core :as S]
    [falcon.schema :as schema]
    [falcon.shell :as shell])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(S/defn create
  "Create a new cluster"
  [cmd :- schema/Command]
  (go
    
    nil))
