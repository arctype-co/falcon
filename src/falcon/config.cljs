(ns falcon.config
  (:require
    [cljs.core.async :as async]
    [schema.core :as S]
    [falcon.schema :as schema])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(def ^:private fs (js/require "fs"))
(def ^:private yamljs (js/require "yamljs"))

(def default-file "cloud/config.yml")

(S/defn read-yml :- schema/Config
  [config-path]
  (let [buf (.readFileSync fs config-path)
        cfg (js->clj (.parse yamljs (str buf)) :keywordize-keys true)]
    cfg))

(S/defn cluster :- schema/ClusterConfig
  "Return a cluster-specific config"
  [options :- schema/ConfigOptions]
  (let [ccfg (get-in options [:config :clusters (keyword (:cluster options))])]
    (if (nil? ccfg)
      (throw (js/Error. (str "Cluster not defined: " (:cluster options))))
      ccfg)))
