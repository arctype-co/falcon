(ns falcon.environment
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
    :default "main"]])

(defn- service-dir
  []
  "cloud/service")

(defn- make-cmd
  [{:keys [environment]} make-args]
  (let [make-params [(str "ENV=" environment)]]
    (concat ["make" "-C" (service-dir)] make-args
          make-params)))

(S/defn create
  "Create a new environment (namespace)"
  [cfg args]
  (println "Creating namespace: " cfg)
  (shell/passthru (make-cmd cfg ["environment/create"])))

(S/defn command
  "Run a command"
  [function
   config :- schema/Config
   {:keys [arguments]} :- schema/Command]
  (let [{:keys [options errors summary]} (cli/parse-opts arguments cli-options)
        {:keys [environment cluster]} options]
    (cond
      (some? errors) 
      (println errors)

      (and (some? cluster) (some? environment))
      (function options arguments)

      :default (println summary))))
