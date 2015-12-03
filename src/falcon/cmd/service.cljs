(ns falcon.cmd.service
  (:require
    [clojure.string :as string]
    [cljs.core.async :as async :refer [<!]]
    [cljs.pprint :refer [pprint]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.core :as core :refer [cloud-path map-keys]]
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

(defn- m4-defs
  [opts {:keys [container-tag controller-tag service] :as params}]
  (merge (m4/defs opts)
         {"SERVICE" service
          "CONTAINER_TAG" container-tag
          "CONTROLLER_TAG" controller-tag}
         (map-keys name (:m4-params (config-ns/service opts service)))))

(defn- make-yml
  [yml-name opts {:keys [service] :as defs}]
  (let []
    (-> (m4/write (m4-defs opts defs)
                  [(cloud-path service (str yml-name ".m4"))]
                  (cloud-path service yml-name))
        (shell/check-status))))

(S/defn list-services
  "List running services"
  [opts args]
  (go
    (<! (kube/services opts args))
    (<! (kube/rc opts args))))

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
      (let [params {:service service}]
      (core/print-summary "Create service:" opts params)
      (go
        (<! (make-yml "service.yml" opts params))
        (<! (kubectl/run opts "create" "-f" (cloud-path service "service.yml"))))))))

(S/defn delete
  "Unload a service config"
  [opts args]
  (require-arguments
    args
    (fn [service]
      (let [params {:service service}]
        (core/print-summary "Delete service:" opts params)
        (go 
          (when-not (:yes opts) (<! (core/safe-wait)))
          (<! (make-yml "service.yml" opts params))
          (<! (kubectl/run opts "delete" "-f" (cloud-path service "service.yml"))))))))

(S/defn create-rc
  "Launch a replication controller"
  [opts args]
  (require-arguments 
    args
    (fn [service]
      (let [{:keys [container-tag]} (config-ns/service opts service)
            controller-tag (or (:tag opts) (core/new-tag))
            params {:service service
                    :controller-tag controller-tag
                    :container-tag container-tag}]
        (core/print-summary "Create replication controler:" opts params)
        (go
          (<! (make-yml "controller.yml" opts params))
          (<! (-> (kubectl/run opts "create" "-f" (cloud-path service "controller.yml"))
                  (shell/check-status))))))))

(S/defn delete-rc
  "Remove a replication controller"
  [opts args]
  (require-arguments 
    args
    (fn [service controller-tag]
      (let [params {:service service
                    :controller-tag controller-tag}]
        (core/print-summary "Delete replication controller:" opts params)
        (go
          (when-not (:yes opts) (<! (core/safe-wait)))
          (<! (make-yml "controller.yml" opts params))
          (<! (kubectl/run opts "delete" "-f" (cloud-path service "controller.yml"))))))))

(S/defn rolling-update
  "Rolling update a replication controller"
  [opts args]
  (require-arguments 
    args
    (fn [service old-controller-tag container-tag]
      (let [controller-tag (core/new-tag)
            full-old-controller-tag (str service "." old-controller-tag)
            params {:service service
                    :controller-tag controller-tag
                    :container-tag container-tag
                    :old-controller-tag old-controller-tag}]
        (core/print-summary "Rolling update replication controller:" opts params)
        (go
          (<! (make-yml "controller.yml" opts params))
          (<! (-> (kubectl/run opts "rolling-update" full-old-controller-tag "-f" (cloud-path service "controller.yml"))
                  (shell/check-status))))))))

(def cli
  {:doc "Service configuration and deployment"
   :options
   [["-t" "--tag <tag>" "Service tag"]
    ["-y" "--yes" "Skip safety prompts" :default false]]
   :commands
   {"create" create
    "delete" delete
    "create-rc" create-rc
    "delete-rc" delete-rc
    "list" list-services
    "status" status
    "rolling-update" rolling-update}})
