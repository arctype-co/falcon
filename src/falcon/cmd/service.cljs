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

(defn- m4-defs
  [opts {:keys [container-tag controller-tag service] :as params}]
  (merge (m4/defs opts)
         {"SERVICE" service
          "CONTAINER_TAG" container-tag
          "CONTROLLER_TAG" controller-tag}))

(defn- make-yml
  [yml-name opts {:keys [service] :as defs}]
  (let []
    (-> (m4/write (m4-defs opts defs)
                  [(service-path service (str yml-name ".m4"))]
                  (service-path service yml-name))
        (shell/check-status))))

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
        (<! (kubectl/run opts "create" "-f" (service-path service "service.yml"))))))))

(S/defn delete
  "Unload a service config"
  [opts args]
  (require-arguments
    args
    (fn [service]
      (let [params {:service service}]
        (core/print-summary "Delete service:" opts params)
        (go 
          (<! (core/safe-wait))
          (<! (make-yml "service.yml" opts params))
          (<! (kubectl/run opts "delete" "-f" (service-path service "service.yml"))))))))

(S/defn create-rc
  "Launch a replication controller"
  [{:keys [environment] :as opts} args]
  (require-arguments 
    args
    (fn [service container-tag]
      (let [controller-tag (core/new-tag)
            params {:service service
                    :controller-tag controller-tag
                    :container-tag container-tag}]
        (core/print-summary "Create replication controler:" opts params)
        (go
          (<! (make-yml "controller.yml" opts params))
          (<! (-> (kubectl/run opts "create" "-f" (service-path service "controller.yml"))
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
          (<! (core/safe-wait))
          (<! (make-yml "controller.yml" opts params))
          (<! (kubectl/run opts "delete" "-f" (service-path service "controller.yml"))))))))

(S/defn rolling-update
  "Rolling update a replication controller"
  [{:keys [environment] :as opts} args]
  (require-arguments 
    args
    (fn [service old-controller-tag container-tag]
      (let [controller-tag (core/new-tag)
            full-old-controller-tag (str service "." old-controller-tag)
            params {:service service
                    :controller-tag controller-tag
                    :old-controller-tag old-controller-tag}]
        (core/print-summary "Rolling update replication controller:" opts params)
        (go
          (<! (make-yml "controller.yml" opts params))
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
