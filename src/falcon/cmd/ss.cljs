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

(defn- running-controller-name
  "Return name of the current controller running for service"
  [{:keys [profile] :as opts} service]
  (go
    (let [selector (str "role=" service (when (some? profile) (str ",profile=" profile)))
          run (kubectl/run (assoc opts :shell-mode :spawn) "--selector" selector "-o" "json" "get" "statefulset")]
      (<! (shell/check-status (:return run)))
      (let [json (<! (:stdout run))
            dict (.parse js/JSON json)
            controller-name (-> dict (aget "items") (aget 0) (aget "metadata") (aget "name"))]
        controller-name))))

(S/defn list
  "List replication controllers' status"
  [opts args]
  (go
    (<! (kubectl/run opts "get" "statefulset"))))

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
                (<! (make-yml yml-file opts params))
                (<! (-> (kubectl/run opts "create" "-f" (species-path service yml-file))
                        (shell/check-status)))))))))))

(S/defn delete
  "Remove a replication controller"
  [opts [service controller-tag]]
  (do-all-profiles
    opts
    (profiles opts service)
    (fn [{:keys [profile] :as opts}]
      (go
        (let [controller-name (if (some? controller-tag)
                                (config-ns/controller-name service profile controller-tag)
                                (<! (running-controller-name opts service)))
              params {:service service
                      :controller-name controller-name}]
          (when (nil? controller-name)
            (throw (js/Error. "Failed to find running controller")))
          (core/print-summary "Delete replication controller:" opts params)
          (when-not (:yes opts) (<! (core/safe-wait)))
          (<! (make-yml yml-file opts params))
          (<! (kubectl/run opts "delete" "statefulset" controller-name)))))))

(S/defn update
  "Replace a replication controller"
  [opts [service]]
  (do-all-profiles
    opts
    (profiles opts service)
    (fn [{:keys [profile] :as opts}]
      (go
        (let [{:keys [container-tag]} (config-ns/service opts service)
              container-tag (or (:container-tag opts) container-tag)
              params {:service service
                      :container-tag container-tag}]
          (core/print-summary "Delete replication controller:" opts params)
          (when-not (:yes opts) (<! (core/safe-wait)))
          (<! (make-yml yml-file opts params))
          (<! (kubectl/run opts "replace" "statefulset" "-f" (species-path service yml-file))))))))

(S/defn scale
  "Scale a replication controller"
  [{:keys [profile] :as opts} [service replicas controller-tag]]
  (go
    (when (nil? replicas)
      (throw (js/Error. "Arguments: [service replicas [controller-tag]]")))
    (let [{:keys [controller-tag]} (config-ns/service opts service)
          controller-name (if (some? controller-tag)
                            (config-ns/controller-name service profile controller-tag)
                            (<! (running-controller-name opts service)))
          params {:service service
                  :controller-name controller-name
                  :replicas replicas}]
      (core/print-summary "Scaling replication controller:" opts params)
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
