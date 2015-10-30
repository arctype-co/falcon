(ns falcon.cluster
  (:require 
    [cljs.pprint :refer [pprint]]
    [cljs.core.async :as async]
    [schema.core :as S]
    [falcon.config :as config]
    [falcon.schema :as schema]
    [falcon.shell :as shell])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(S/defn create
  "Create a new cluster"
  [cfg :- schema/Config
   cmd :- schema/Command]
  (let [cluster-config (config/cluster cfg (:options cmd))]
    (println "Creating cluster with configuration:")
    (pprint cluster-config))
  #_(go
    (shell/exec
      {:env
       ""})
    
    nil))
