(ns falcon.cmd.environment
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

(def cli
  {:doc "Environment setup"
   :options [["-e" "--environment <env>" "Environment"
              :default "local"]
             ["-x" "--cluster <name>" "Cluster name"
              :default "main"]]
   :commands {"create" create
              "delete" delete}})
