(ns falcon.service
  (:require
    [clojure.string :as string]
    [cljs.core.async :as async :refer [<!]]
    [cljs.pprint :refer [pprint]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.schema :as schema]
    [falcon.shell :as shell]
    [falcon.shell.make :as make]
    [falcon.shell.kubectl :as kubectl])
  (:require-macros
    [falcon.core :refer [require-arguments]]
    [cljs.core.async.macros :refer [go]]))

(def ^:private cli-options
  [["-e" "--environment <env>" "Environment"
    :default "local"]
   ["-x" "--cluster <name>" "Cluster name"
    :default "main"]
   ["-s" "--service <name>" "Service name"]])

(defn- service-path
  [& path]
  (string/join "/" (concat ["cloud/service"] path)))

(S/defn create
  "Load a service config"
  [{:keys [environment service] :as cfg} args]
  (println "Create service from config:")
  (pprint cfg)
  (go
    (<! (make/run {"ENV" environment} ["-C" (service-path) (str service "/service.yml")]))
    (<! (kubectl/run cfg "create" "-f" (service-path service "service.yml")))))

(S/defn delete
  "Unload a service config"
  [{:keys [environment service] :as cfg} args]
  (println "Delete service from config:")
  (pprint cfg)
  (println "Waiting 10 seconds...")
  (go 
    (<! (async/timeout 10000))
    (<! (make/run {"ENV" environment} ["-C" (service-path) (str service "/service.yml")]))
    (<! (kubectl/run cfg "delete" "-f" (service-path service "service.yml")))))

(S/defn deploy
  "Deloy a replication controller"
  [{:keys [environment service] :as cfg} args]
  (require-arguments 
    args
    (fn [container-tag controller-tag]
      (println "Deploying service controller:")
      (pprint cfg)
      (go
        (<! (make/run {"ENV" environment
                       "CONTAINER_TAG" container-tag
                       "CONTROLLER_TAG" controller-tag}
                      ["-C" (service-path) (str service "/controller.yml")]))
        (<! (kubectl/run cfg "create" "-f" (service-path service "controller.yml")))))))

(S/defn command
  "Run a service command"
  [function
   config :- schema/Config
   {:keys [arguments]} :- schema/Command]
  (let [{:keys [arguments options errors summary]} (cli/parse-opts arguments cli-options)
        {:keys [environment cluster service]} options]
    (cond
      (some? errors) 
      (println errors)

      (and (some? cluster) (some? service))
      (function #_(get-in config [environment "clusters" cluster "services" service])
               options (vec (rest arguments)))

      :default (println summary))))
