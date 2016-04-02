(ns falcon.cmd.service
  (:refer-clojure :exclude [list])
  (:require
    [clojure.string :as string]
    [cljs.core.async :as async :refer [<!]]
    [cljs.pprint :refer [pprint]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.core :as core :refer [species-path map-keys do-all-profiles]]
    [falcon.config :as config-ns]
    [falcon.schema :as schema]
    [falcon.shell :as shell]
    [falcon.shell.m4 :as m4]
    [falcon.shell.make :as make]
    [falcon.shell.kubectl :as kubectl])
  (:require-macros
    [falcon.core :refer [require-arguments]]
    [cljs.core.async.macros :refer [go]]))

(defn- profiles
  [opts svc]
  (keys (:profiles (config-ns/service opts svc))))

(defn- make-yml
  [yml-name opts {:keys [service] :as defs}]
  (let []
    (-> (m4/write (m4/defs opts defs)
                  [(species-path service (str yml-name ".m4"))]
                  (species-path service yml-name))
        (shell/check-status))))

(S/defn list
  "List running services"
  [opts args]
  (go
    (<! (kubectl/run opts "get" "services"))))

(S/defn status
  "Show all applications' status"
  [opts args]
  (go
    (<! (kubectl/run opts "get" "services"))
    (println)
    (<! (kubectl/run opts "get" "rc"))
    (println)
    (<! (kubectl/run opts "get" "pods"))))

(S/defn create
  "Load a service config"
  [opts args]
  (require-arguments
    args
    (fn [service]
      (do-all-profiles opts (profiles opts service)
        (fn [opts]
          (let [params {:service service}]
            (core/print-summary "Create service:" opts params)
            (go
              (<! (make-yml "service.yml" opts params))
              (<! (kubectl/run opts "create" "-f" (species-path service "service.yml"))))))))))

(S/defn delete
  "Unload a service config"
  [opts args]
  (require-arguments
    args
    (fn [service]
      (do-all-profiles opts (profiles opts service)
        (fn [opts]
          (let [params {:service service}]
            (core/print-summary "Delete service:" opts params)
            (go 
              (when-not (:yes opts) (<! (core/safe-wait)))
              (<! (make-yml "service.yml" opts params))
              (<! (kubectl/run opts "delete" "-f" (species-path service "service.yml"))))))))))

(def cli
  {:doc "Service configuration and deployment"
   :options
   [["-a" "--all" "Run command for all profiles"]
    ["-e" "--environment <env>" "Environment"]
    ["-p" "--profile <profile>" "Service profile"]
    ["-y" "--yes" "Skip safety prompts" :default false]]
   :commands
   {"create" create
    "delete" delete
    "list" list
    "status" status}})
