(ns falcon.cmd.service
  (:require
    [clojure.string :as string]
    [cljs.core.async :as async :refer [<!]]
    [cljs.pprint :refer [pprint]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.core :as core]
    [falcon.schema :as schema]
    [falcon.shell :as shell]
    [falcon.shell.m4 :as m4]
    [falcon.shell.make :as make]
    [falcon.shell.kubectl :as kubectl])
  (:require-macros
    [falcon.core :refer [require-arguments]]
    [cljs.core.async.macros :refer [go]]))

(defn- service-path
  [& path]
  (string/join "/" (concat ["cloud/service"] path)))

(defn- make-service-yml
  [opts service]
  (let [defs (m4/base-defs opts)]
    (m4/write defs [(service-path service "service.yml.m4")] (service-path service "service.yml"))))

(S/defn create
  "Load a service config"
  [opts args]
  (require-arguments
    (rest args)
    (fn [service]
      (core/print-summary "Create service:" opts {:service service})
      (go
        (<! (make-service-yml opts service))
        (<! (kubectl/run opts "create" "-f" (service-path service "service.yml")))))))

(S/defn delete
  "Unload a service config"
  [opts args]
  (require-arguments
    (rest args)
    (fn [service]
      (core/print-summary "Delete service:" opts {:service service})
      (go 
        (<! (core/safe-wait))
        (<! (make-service-yml opts service))
        (<! (kubectl/run opts "delete" "-f" (service-path service "service.yml")))))))

(S/defn create-rc
  "Launch a replication controller"
  [{:keys [environment] :as opts} args]
  (require-arguments 
    (rest args)
    (fn [service container-tag]
      (let [controller-tag (core/new-tag)]
        (core/print-summary "Create replication controler:" opts {:service service
                                                                  :controller-tag controller-tag
                                                                  :container-tag container-tag})
        (go
          (<! (-> (make/run {"ENV" environment
                             "CONTAINER_TAG" container-tag
                             "CONTROLLER_TAG" controller-tag}
                            ["-C" (service-path) (str service "/controller.yml")])
                  (shell/check-status)))
          (<! (-> (kubectl/run opts "create" "-f" (service-path service "controller.yml"))
                  (shell/check-status))))))))

(S/defn delete-rc
  "Remove a replication controller"
  [opts args]
  (require-arguments 
    (rest args)
    (fn [service controller-tag]
      (core/print-summary "Delete replication controller:" opts 
                          {:service service
                           :controller-tag controller-tag})
      (go
        (<! (core/safe-wait))
        (<! (kubectl/run opts "delete" "rc" (str service "." controller-tag)))))))

(S/defn rolling-update
  "Rolling update a replication controller"
  [{:keys [environment] :as opts} args]
  (require-arguments 
    (rest args)
    (fn [service old-controller-tag container-tag]
      (let [controller-tag (core/new-tag)
            full-old-controller-tag (str service "." old-controller-tag)]
        (core/print-summary "Rolling update replication controller:" opts
                            {:service service
                             :controller-tag controller-tag
                             :old-controller-tag old-controller-tag})
        (go
          (<! (-> (make/run {"ENV" environment
                             "CONTAINER_TAG" container-tag
                             "CONTROLLER_TAG" controller-tag}
                            ["-C" (service-path) (str service "/controller.yml")])
                  (shell/check-status)))
          (<! (-> (kubectl/run opts "rolling-update" full-old-controller-tag "-f" (service-path service "controller.yml"))
                  (shell/check-status))))))))

(def cli
  {:doc "Service configuration and deployment"
   :options
   []
   :commands
   {"create" create
    "delete" delete
    "create-rc" create-rc
    "delete-rc" delete-rc
    "rolling-update" "rolling-update"}})
