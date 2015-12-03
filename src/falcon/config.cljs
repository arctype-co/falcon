(ns falcon.config
  (:require
    [cljs.core.async :as async]
    [schema.core :as S]
    [falcon.core :refer [rmerge]]
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

(S/defn container :- schema/ContainerConfig
  "Returns a container-specific config"
  [options :- schema/ConfigOptions ctnr]
  (or (get-in options [:config :containers (keyword ctnr)]) {}))

(S/defn service :- schema/ServiceConfig
  "Returns a service-specific config in it's environment"
  [{:keys [environment tag] :as options} :- schema/ConfigOptions svc]
  (let [svc-cfg (or (get-in options [:config :environments (keyword environment) :services (keyword svc)]) {})]
    (when (some? tag)
      (rmerge svc-cfg (get-in svc-cfg [:tags (keyword tag)])))))
