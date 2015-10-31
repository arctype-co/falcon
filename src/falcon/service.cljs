(ns falcon.service
  (:require
    [clojure.string :as string]
    [cljs.core.async :as async :refer [<!]]
    [cljs.pprint :refer [pprint]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.schema :as schema]
    [falcon.shell :as shell]
    [falcon.shell.kubectl :as kubectl])
  (:require-macros
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

(defn- make-cmd
  [{:keys [environment]} make-args]
  (let [make-params [(str "ENV=" environment)]]
    (concat ["make" "-C" (service-path)] make-args
          make-params)))

(S/defn create
  "Load a service config"
  [{:keys [service] :as cfg} args]
  (println "Create service from config:")
  (pprint cfg)
  (go
    (<! (shell/passthru (make-cmd cfg [(str service "/service.yml")])))
    (<! (kubectl/run cfg "create" "-f" (service-path service "service.yml")))))

(S/defn delete
  "Unload a service config"
  [{:keys [service] :as cfg} args]
  (println "Delete service from config:")
  (pprint cfg)
  (println "Waiting 10 seconds...")
  (go 
    (<! (async/timeout 10000))
    (<! (shell/passthru (make-cmd cfg [(str service "/service.yml")])))
    (<! (kubectl/run cfg "delete" "-f" (service-path service "service.yml")))))

(S/defn command
  "Run a service command"
  [function
   config :- schema/Config
   {:keys [arguments]} :- schema/Command]
  (let [{:keys [options errors summary]} (cli/parse-opts arguments cli-options)
        {:keys [environment cluster service]} options]
    (cond
      (some? errors) 
      (println errors)

      (and (some? cluster) (some? service))
      (function #_(get-in config [environment "clusters" cluster "services" service])
               options arguments)

      :default (println summary))))
