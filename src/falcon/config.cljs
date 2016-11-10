(ns falcon.config
  (:require
    [cljs.core.async :as async]
    [schema.core :as S]
    [falcon.util :refer [rmerge]]
    [falcon.schema :as schema])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(def ^:private fs (js/require "fs"))
(def ^:private yamljs (js/require "yamljs"))

(def default-file "config.yml")

(defn- parse-yml
  [buf]
  (try (.parse yamljs (str buf))
       (catch js/Error e
         (throw (js/Error. (str "Failed to parse config.yml: " (.-message e)))))))

(S/defn read-yml :- schema/Config
  [config-path]
  (let [buf (.readFileSync fs config-path)
        cfg (js->clj (parse-yml buf) :keywordize-keys true)]
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
  [{:keys [repository] :as options} :- schema/ConfigOptions
   ctnr :- S/Str]
  (merge {}
         (get-in options [:config :containers (keyword ctnr)])
         (get-in options [:config :containers (keyword (str repository "/" ctnr))])))

(defn- find-registry-id
  [options container-name]
  (:registry-id (container options container-name)))

(defn full-container-tag
  [{:keys [repository] :as opts} container-name tag]
  (let [registry-id (find-registry-id opts container-name)]
    (if (some? registry-id)
      (str registry-id ":" tag)
      (str repository "/" (core/species-name container-name) ":" tag))))

(S/defn service :- schema/ServiceConfig
  "Returns a service-specific config in it's environment"
  [{:keys [environment profile repository] :as options} :- schema/ConfigOptions
   svc :- S/Str]
  (let [svc-cfg (merge {}
                       (get-in options [:config :environments (keyword environment) :services (keyword svc)])
                       (get-in options [:config :environments (keyword environment) :services (keyword (str repository "/" svc))]))]
    (if (some? profile)
      (rmerge svc-cfg (get-in svc-cfg [:profiles (keyword profile)]))
      svc-cfg)))

(S/defn controller-name :- S/Str
  "Returns a controller name, in the form <service>{-<profile}-<tag>, where profile is optional."
  [service :- S/Str
   profile :- (S/maybe S/Str)
   tag :- S/Str]
  (str service
       (when (some? profile) (str "-" profile))
       "-" tag))
