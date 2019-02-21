(ns falcon.cmd.ss
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

(def ^:private yml-file "statefulset.yml")

(S/defn list
  "List stateful sets"
  [opts args]
  (go
    (<! (kubectl/run opts "get" "statefulset"))))

(S/defn create
  "Launch a stateful set"
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
              (core/print-summary "Create stateful set:" opts params)
              (go
                (<! (make-yml yml-file opts params))
                (<! (-> (kubectl/run opts "create" "-f" (species-path service yml-file))
                        (shell/check-status)))))))))))

(S/defn delete
  "Remove a stateful set"
  [opts [service]]
  (do-all-profiles
    opts
    (profiles opts service)
    (fn [{:keys [profile] :as opts}]
      (go
        (let [controller-name (config-ns/controller-name service profile nil)
              params {:service service
                      :controller-name controller-name}]
          (core/print-summary "Delete stateful set:" opts params)
          (when-not (:yes opts) (<! (core/safe-wait)))
          (<! (make-yml yml-file opts params))
          (<! (kubectl/run opts "delete" "statefulset" controller-name)))))))

(S/defn update
  "Replace a stateful set"
  [opts [service]]
  (do-all-profiles
    opts
    (profiles opts service)
    (fn [{:keys [profile] :as opts}]
      (go
        (let [params {:service service
                      :profile profile}]
          (core/print-summary "Delete stateful set" opts params)
          (when-not (:yes opts) (<! (core/safe-wait)))
          (<! (make-yml yml-file opts params))
          (<! (kubectl/run opts "replace" "statefulset" "-f" (species-path service yml-file))))))))

(S/defn scale
  "Scale a stateful set"
  [{:keys [profile] :as opts} [service replicas]]
  (go
    (when (nil? replicas)
      (throw (js/Error. "Arguments: [service replicas]")))
    (let [controller-name (config-ns/controller-name service profile nil)
          params {:service service
                  :controller-name controller-name
                  :replicas replicas}]
      (core/print-summary "Scaling stateful set" opts params)
      (<! (make-yml yml-file opts params))
      (<! (kubectl/run opts "scale" "statefulset" (str "--replicas=" replicas) controller-name)))))

(def cli
  {:doc "Service configuration and deployment"
   :options
   (core/cli-options
     [["-a" "--all" "Run command for all profiles"]
      ["-c" "--container-tag <tag>" "Container tag"]
      ["-e" "--environment <env>" "Environment"]
      ["-p" "--profile <profile>" "Service profile"]
      ["-y" "--yes" "Skip safety prompts" :default false]])
   :commands
   {"create" create
    "delete" delete
    "update" update
    "list" list
    "scale" scale}})
