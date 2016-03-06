(ns falcon.cmd.service
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
    [falcon.shell.kubectl :as kubectl]
    [falcon.cmd.kube :as kube])
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

(S/defn controllers
  "Get replication controllers status"
  [opts args]
  (go
    (<! (kubectl/run opts "get" "rc"))))

(S/defn pods
  "Get pods status"
  [opts args]
  (go
    (<! (kubectl/run opts "get" "-o" "wide" "pods"))))

(S/defn list-services
  "List running services"
  [opts args]
  (go
    (<! (kube/services opts args))))

(S/defn status
  "Show all applications' status"
  [opts args]
  (go
    (<! (kube/services opts args))
    (println)
    (<! (kube/rc opts args))
    (println)
    (<! (kube/pods opts args))))

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

(S/defn create-rc
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

(S/defn delete-rc
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

(S/defn update-rc
  "Replace a replication controller"
  [opts args]
  (go (<! (-> (delete-rc opts args)
              (shell/check-status)))
      (<! (-> (create-rc opts (take 1 args))
              (shell/check-status)))))

(S/defn rolling-update
  "Rolling update a replication controller"
  [opts args]
  (require-arguments 
    args
    (fn [service old-controller-tag container-tag]
      (let [controller-tag (core/new-tag)
            params {:service service
                    :controller-tag controller-tag
                    :container-tag container-tag
                    :old-controller-tag old-controller-tag}]
        (core/print-summary "Rolling update replication controller:" opts params)
        (go
          (<! (make-yml "controller.yml" opts params))
          (<! (-> (kubectl/run opts "rolling-update" old-controller-tag "-f" (species-path service "controller.yml"))
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
    "create-rc" create-rc
    "delete-rc" delete-rc
    "update-rc" update-rc
    "list" list-services
    "controllers" controllers
    "pods" pods
    "scale" scale
    "status" status
    "rolling-update" rolling-update}})
