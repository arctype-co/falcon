(ns falcon.config
  (:require
    [cljs.core.async :as async]
    [schema.core :as S]
    [falcon.schema :as schema])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(def ^:private fs (js/require "fs"))
(def ^:private yamljs (js/require "yamljs"))

(S/defn read-yml :- schema/Config
  [config-path]
  (let [buf (.readFileSync fs config-path)
        cfg (js->clj (.parse yamljs (str buf)))]
    cfg))

(S/defn cluster :- schema/ClusterConfig
  "Return the cluster configuration"
  [config :- schema/Config
   {:keys [environment cluster]} :- schema/Options]
  (get-in config [environment "clusters" cluster]))
