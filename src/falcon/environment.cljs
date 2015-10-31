(ns falcon.environment
  (:require
    [clojure.string :as string]
    [cljs.core.async :as async :refer [<!]]
    [cljs.pprint :refer [pprint]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.schema :as schema]
    [falcon.shell :as shell]
    [falcon.shell.kubectl :as kubectl]
    [falcon.shell.make :as make])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(def ^:private cli-options
  [["-e" "--environment <env>" "Environment"
    :default "local"]
   ["-x" "--cluster <name>" "Cluster name"
    :default "main"]])

(defn- make-path
  [& path]
  (string/join "/" (concat ["cloud/service"] path)))

(S/defn create
  "Create a new environment (namespace)"
  [{:keys [environment] :as cfg} args]
  (println "Creating namespace: " cfg)
  (go 
    (<! (make/run {"ENV" environment}
              ["-C" (make-path) "environment/namespace.yml"]))
    (<! (kubectl/run cfg "create" "-f" (make-path "environment" "namespace.yml")))))

(S/defn delete
  "Delete an environment (namespace)"
  [{:keys [environment] :as cfg} args]
  (println "Deleting namespace: " cfg)
  (println "Waiting 10 seconds...")
  (go 
    (<! (async/timeout 10000))
    (<! (make/run {"ENV" environment}
                  ["-C" (make-path) "environment/namespace.yml"]))
    (<! (kubectl/run cfg "delete" "-f" (make-path "environment" "namespace.yml")))))

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
