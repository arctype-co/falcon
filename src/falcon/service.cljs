(ns falcon.service
  (:require
    [cljs.pprint :refer [pprint]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.schema :as schema]
    [falcon.shell :as shell]))

(def ^:private cli-options
  [["-e" "--environment <env>" "Environment"
    :default "local"]
   ["-x" "--cluster <name>" "Cluster name"
    :default "main"]
   ["-s" "--service <name>" "Service name"]])

(defn- service-dir
  []
  "cloud/service")

(defn- make-cmd
  [{:keys [environment]} make-args]
  (let [make-params [(str "ENV=" environment)]]
    (concat ["make" "-C" (service-dir)] make-args
          make-params)))

(S/defn create
  "Load a service config"
  [{:keys [cluster environment service] :as cfg} args]
  (println "Create service from config:")
  (pprint cfg)
  (shell/passthru (make-cmd cfg [(str service "/create")])))

(S/defn create-env
  "Create a new environment (namespace)"
  [{:keys [cluster environment]} args]
  (println "Creating namespace: " environment)
  (shell/passthru (make-cmd cfg ["envoronment/create"])))

(S/defn command
  "Run a service command"
  [function
   config :- schema/Config
   {:keys [arguments]} :- schema/Command]
  (let [{:keys [options errors summary]} (cli/parse-opts arguments cli-options)
        {:keys [environment cluster service]} options]
    (cond
      (some? errors) 
      (println errors)

      (and (some? cluster) (some? service))
      (function #_(get-in config [environment "clusters" cluster "services" service])
               options arguments)

      :default (println summary))))
