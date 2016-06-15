(ns falcon.cmd.rc
  (:refer-clojure :exclude [list update])
  (:require
    [clojure.string :as string]
    [cljs.core.async :as async :refer [<!]]
    [cljs.pprint :refer [pprint]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.core :as core :refer [profiles species-path map-keys do-all-profiles]]
    [falcon.config :as config-ns]
    [falcon.schema :as schema]
    [falcon.shell :as shell]
    [falcon.shell.m4 :as m4]
    [falcon.shell.make :as make]
    [falcon.shell.kubectl :as kubectl]
    [falcon.template :refer [make-yml]])
  (:require-macros
    [falcon.core :refer [require-arguments]]
    [cljs.core.async.macros :refer [go]]))

(S/defn list
  "List replication controllers' status"
  [opts args]
  (go
    (<! (kubectl/run opts "get" "rc"))))

(S/defn create
  "Launch a replication controller"
  [opts args]
  (require-arguments 
    args
    (fn [service]
      (let [controller-tag (core/new-tag)]
        (do-all-profiles opts (profiles opts service)
          (fn [opts]
            (let [{:keys [container-tag]} (config-ns/service opts service)
                  container-tag (or (:container-tag opts) container-tag)
                  params {:service service
                          :controller-tag controller-tag
                          :container-tag container-tag}]
              (core/print-summary "Create replication controler:" opts params)
              (go
                (<! (make-yml "controller.yml" opts params))
                (<! (-> (kubectl/run opts "create" "-f" (species-path service "controller.yml"))
                        (shell/check-status)))))))))))

(S/defn delete
  "Remove a replication controller"
  [opts args]
  (require-arguments 
    args
    (fn [service controller-tag]
      (do-all-profiles opts (profiles opts service)
        (fn [opts]
          (let [{:keys [container-tag]} (config-ns/service opts service)
                container-tag (or (:container-tag opts) container-tag)
                params {:service service
                        :controller-tag controller-tag
                        :container-tag container-tag}]
            (core/print-summary "Delete replication controller:" opts params)
            (go
              (when-not (:yes opts) (<! (core/safe-wait)))
              (<! (make-yml "controller.yml" opts params))
              (<! (kubectl/run opts "delete" "-f" (species-path service "controller.yml"))))))))))

(S/defn update
  "Replace a replication controller"
  [opts args]
  (go (<! (-> (delete opts args)
              (shell/check-status)))
      (<! (-> (create opts (take 1 args))
              (shell/check-status)))))

(S/defn roll
  "Rolling update a replication controller"
  [opts args]
  (require-arguments 
    args
    (fn [service old-controller-tag]
      (let [{:keys [container-tag profile]} (config-ns/service opts service)
            container-tag (or (:container-tag opts) container-tag)
            controller-tag (core/new-tag)
            params {:service service
                    :profile profile
                    :controller-tag controller-tag
                    :container-tag container-tag
                    :old-controller-tag old-controller-tag}
            old-controller-name (config-ns/controller-name service profile old-controller-tag)]
        (core/print-summary "Rolling update replication controller:" opts params)
        (go
          (<! (make-yml "controller.yml" opts params))
          (<! (-> (kubectl/run opts "rolling-update" old-controller-name "-f" (species-path service "controller.yml"))
                  (shell/check-status))))))))

(S/defn scale
  "Scale a replication controller"
  [opts args]
  (require-arguments 
    args
    (fn [service controller-tag replicas]
      (let [{:keys [container-tag]} (config-ns/service opts service)
            params {:service service
                    :container-tag (or (:container-tag opts) container-tag)
                    :controller-tag controller-tag
                    :replicas replicas}]
        (core/print-summary "Scaling replication controller:" opts params)
        (go
          (<! (make-yml "controller.yml" opts params))
          (<! (kubectl/run opts "scale" (str "--replicas=" replicas) "-f" (species-path service "controller.yml"))))))))

(def cli
  {:doc "Service configuration and deployment"
   :options
   [["-a" "--all" "Run command for all profiles"]
    ["-c" "--container-tag <tag>" "Container tag"]
    ["-e" "--environment <env>" "Environment"]
    ["-p" "--profile <profile>" "Service profile"]
    ["-y" "--yes" "Skip safety prompts" :default false]]
   :commands
   {"create" create
    "delete" delete
    "update" update
    "list" list
    "scale" scale
    "roll" roll}})
