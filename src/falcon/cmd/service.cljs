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
    [falcon.shell.make :as make]
    [falcon.shell.kubectl :as kubectl])
  (:require-macros
    [falcon.core :refer [require-arguments]]
    [cljs.core.async.macros :refer [go]]))

(defn- service-path
  [& path]
  (string/join "/" (concat ["cloud/service"] path)))

(S/defn create
  "Load a service config"
  [{:keys [environment] :as cfg} args]
  (require-arguments
    (rest args)
    (fn [service]
      (println "Create service from config:")
      (pprint cfg)
      (go
        (<! (make/run {"ENV" environment} ["-C" (service-path) (str service "/service.yml")]))
        (<! (kubectl/run cfg "create" "-f" (service-path service "service.yml")))))))

(S/defn delete
  "Unload a service config"
  [{:keys [environment] :as cfg} args]
  (require-arguments
    (rest args)
    (fn [service]
      (println "Delete service from config:")
      (pprint cfg)
      (go 
        (<! (core/safe-wait))
        (<! (make/run {"ENV" environment} ["-C" (service-path) (str service "/service.yml")]))
        (<! (kubectl/run cfg "delete" "-f" (service-path service "service.yml")))))))

(S/defn create-rc
  "Launch a replication controller"
  [{:keys [environment] :as cfg} args]
  (require-arguments 
    (rest args)
    (fn [service container-tag]
      (println "Creating service controller:")
      (pprint cfg)
      (let [controller-tag (core/new-tag)]
        (go
          (<! (-> (make/run {"ENV" environment
                             "CONTAINER_TAG" container-tag
                             "CONTROLLER_TAG" controller-tag}
                            ["-C" (service-path) (str service "/controller.yml")])
                  (shell/check-status)))
          (<! (-> (kubectl/run cfg "create" "-f" (service-path service "controller.yml"))
                  (shell/check-status))))))))

(S/defn delete-rc
  "Remove a replication controller"
  [{:keys [environment] :as cfg} args]
  (require-arguments 
    (rest args)
    (fn [service controller-tag]
      (println "Removing service controller:")
      (pprint cfg)
      (println "Controller:" controller-tag)
      (go
        (<! (core/safe-wait))
        (<! (kubectl/run cfg "delete" "rc" (str service "." controller-tag)))))))

(S/defn rolling-update
  "Rolling update a replication controller"
  [{:keys [environment] :as cfg} args]
  (require-arguments 
    (rest args)
    (fn [service old-controller-tag container-tag]
      (println "Rolling update service controller:")
      (pprint cfg)
      (let [controller-tag (core/new-tag)
            full-old-controller-tag (str service "." old-controller-tag)]
        (go
          (<! (-> (make/run {"ENV" environment
                             "CONTAINER_TAG" container-tag
                             "CONTROLLER_TAG" controller-tag}
                            ["-C" (service-path) (str service "/controller.yml")])
                  (shell/check-status)))
          (<! (-> (kubectl/run cfg "rolling-update" full-old-controller-tag "-f" (service-path service "controller.yml"))
                  (shell/check-status))))))))

(def cli
  {:doc "Service configuration and deployment"
   :options
   [["-e" "--environment <env>" "Environment"
     :default "local"]
    ["-x" "--cluster <name>" "Cluster name"
     :default "main"]]
   :commands
   {"create" create
    "delete" delete
    "create-rc" create-rc
    "delete-rc" delete-rc
    "rolling-update" "rolling-update"}})
