(ns falcon.cluster
  (:require 
    [cljs.pprint :refer [pprint]]
    [cljs.core.async :as async]
    [schema.core :as S]
    [falcon.config :as config]
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
    "BASE_IP_ADDR" (str get ccfg "base-ip")}})

(defn- vagrant-cmd
  [ccfg cmd]
  (go
    (-> (shell/passthru (concat ["vagrant"] cmd) (vagrant-options ccfg)))))

(S/defn create
  "Create a new cluster"
  [cfg :- schema/Config
   cmd :- schema/Command]
  (let [ccfg (config/cluster cfg (:options cmd))]
    (println "Creating cluster with configuration:")
    (pprint ccfg)
    (vagrant-cmd ccfg ["up"])))

(S/defn destroy
  "Destroy a cluster"
  [cfg :- schema/Config
   cmd :- schema/Command]
  (let [ccfg (config/cluster cfg (:options cmd))]
    (println "About to DESTROY cluster with configuration:")
    (pprint ccfg)
    (println "Waiting 10 seconds...")
    (go
      (async/<! (async/timeout 10000))
      (vagrant-cmd ccfg ["destroy"]))))

(S/defn status
  "Print cluster status"
  [cfg :- schema/Config
   cmd :- schema/Command]
  (vagrant-cmd (config/cluster cfg (:options cmd)) ["status"]))
