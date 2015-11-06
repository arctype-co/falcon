(ns falcon.cmd.cluster
  (:require 
    [cljs.pprint :refer [pprint]]
    [cljs.core.async :as async :refer [<!]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.config :as config]
    [falcon.core :as core]
    [falcon.schema :as schema]
    [falcon.shell :as shell])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(defn- vagrant-dir
  []
  (str (.cwd js/process) "/cloud/cluster/kubernetes-vagrant-coreos-cluster"))

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
    "BASE_IP_ADDR" (str get ccfg "base-ip")
    }})

(S/defn ^:private cluster-config :- schema/ClusterConfig
  [options]
  (get-in options [:config (:environment options) "clusters" (:cluster options)]))

(defn- vagrant-cmd
  [ccfg cmd]
  (shell/passthru (concat ["vagrant"] cmd) (vagrant-options ccfg)))

(S/defn create
  "Create a new cluster"
  [options :- schema/Options args]
  (let [ccfg (cluster-config options)]
    (println "Creating cluster with configuration:")
    (pprint ccfg)
    (vagrant-cmd ccfg ["up"])))

(def ^{:doc "Bring an existing cluster online"} up create)

(S/defn down
  "Bring a cluster offline"
  [options :- schema/Options args]
  (let [ccfg (cluster-config options)]
    (println "Bringing cluster offline:")
    (pprint ccfg)
    (go (<! (core/safe-wait))
        (<! (vagrant-cmd ccfg ["halt"])))))

(S/defn destroy
  "Destroy a cluster"
  [options :- schema/Options args]
  (let [ccfg (cluster-config options)]
    (println "About to DESTROY cluster with configuration:")
    (pprint ccfg)
    (go (<! (core/safe-wait))
        (<! (vagrant-cmd ccfg ["destroy"])))))

(S/defn status
  "Print cluster status"
  [options :- schema/Options args]
  (let [ccfg (cluster-config options)]
    (println ccfg)
    (vagrant-cmd ccfg ["status"])))

(def cli
  {:doc "Run a cluster command"
   :options [["-e" "--environment <env>" "Environment"
              :default "local"]
             ["-x" "--cluster <name>" "Cluster name"
              :default "main"]]
   :commands {"create" create
              "destroy" destroy
              "up" up
              "down" down
              "status" status}})
