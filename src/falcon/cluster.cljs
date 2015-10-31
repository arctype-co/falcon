(ns falcon.cluster
  (:require 
    [cljs.pprint :refer [pprint]]
    [cljs.core.async :as async]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.config :as config]
    [falcon.schema :as schema]
    [falcon.shell :as shell])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(defn- vagrant-dir
  []
  (str (.cwd js/process) "/cloud/cluster/kubernetes-vagrant-coreos-cluster"))

(def ^:private cluster-options
  [["-e" "--environment <env>" "Environment"
    :default "local"]
   ["-x" "--cluster <name>" "Cluster name"]])

(S/defn ^:private vagrant-options
  [ccfg :- schema/ClusterConfig]
  {:cwd (vagrant-dir) 
   :env
   {"NODES" (str (get ccfg "nodes"))
    "CHANNEL" (str (get ccfg "coreos-channel"))
    "MASTER_MEM" (str (get ccfg "master-mem-mb"))
    "MASTER_CPUS" (str (get ccfg "master-cpus"))
    "NODE_MEM" (str (get ccfg "node-mem-mb"))
    "NODE_CPUS" (str (get ccfg "node-cpus"))
    "USE_KUBE_UI" (str (get ccfg "kube-ui"))
    "BASE_IP_ADDR" (str get ccfg "base-ip")}})

(defn- vagrant-cmd
  [ccfg cmd]
  (go
    (-> (shell/passthru (concat ["vagrant"] cmd) (vagrant-options ccfg)))))

(S/defn ^:private cluster-cmd
  "Return the cluster configuration"
  [function
   config :- schema/Config
   {:keys [arguments]} :- schema/Command]
  (let [{:keys [options errors summary]} (cli/parse-opts arguments cluster-options)
        {:keys [environment cluster]} options]
    (cond
      (some? errors) (println errors)
      (some? cluster) (function (get-in config [environment "clusters" cluster]) arguments)
      :default (println summary))))

(def ^{:doc "Create a new cluster"} create
  (partial cluster-cmd
           (fn [ccfg args]
             (println "Creating cluster with configuration:")
             (pprint ccfg)
             (vagrant-cmd ccfg ["up"]))))

(def ^{:doc "Destroy a cluster"} destroy
  (partial cluster-cmd 
           (fn [ccfg args]
             (println "About to DESTROY cluster with configuration:")
             (pprint ccfg)
             (println "Waiting 10 seconds...")
             (go
               (async/<! (async/timeout 10000))
               (vagrant-cmd ccfg ["destroy"])))))

(def ^{:doc "Print cluster status"} status
  (partial cluster-cmd 
           (fn [ccfg args] (vagrant-cmd ccfg ["status"]))))
