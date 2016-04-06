(ns ^{:doc "Daemon set controls"}
  falcon.cmd.ds
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
  "List Daemon sets' status"
  [opts args]
  (go
    (<! (kubectl/run opts "get" "ds"))))

(S/defn create
  "Launch a daemon set"
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
              (core/print-summary "Create daemon set:" opts params)
              (go
                (<! (make-yml "daemon.yml" opts params))
                (<! (-> (kubectl/run opts "create" "-f" (species-path service "daemon.yml"))
                        (shell/check-status)))))))))))

(S/defn delete
  "Remove a daemon set"
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
            (core/print-summary "Delete daemon set:" opts params)
            (go
              (when-not (:yes opts) (<! (core/safe-wait)))
              (<! (make-yml "daemon.yml" opts params))
              (<! (kubectl/run opts "delete" "-f" (species-path service "daemon.yml"))))))))))

(S/defn update
  "Replace a daemon set"
  [opts args]
  (go (<! (-> (delete opts args)
              (shell/check-status)))
      (<! (-> (create opts (take 1 args))
              (shell/check-status)))))

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
    "list" list}})
