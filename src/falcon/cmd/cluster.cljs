(ns falcon.cmd.cluster
  (:require 
    [cljs.pprint :refer [pprint]]
    [cljs.core.async :as async :refer [<!]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.config :as config]
    [falcon.core :as core :refer-macros [require-arguments]]
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
   (cond->
     {"NODES" (str (:nodes ccfg))
      "CHANNEL" (str (:coreos-channel ccfg))
      "MASTER_MEM" (str (:master-mem-mb ccfg))
      "MASTER_CPUS" (str (:master-cpus ccfg))
      "NODE_MEM" (str (:node-mem-mb ccfg))
      "NODE_CPUS" (str (:node-cpus ccfg))
      "USE_DOCKERCFG" "true" ; use docker config from the host machine
      "USE_KUBE_UI" (str (:kube-ui ccfg))}
     (some? (:dockercfg ccfg)) (assoc "DOCKERCFG" (str (:dockercfg ccfg)))
     (some? (:base-ip ccfg)) (assoc "BASE_IP_ADDR" (str (:base-ip ccfg))))})

(S/defn ^:private cluster-config :- schema/ClusterConfig
  [options]
  (let [ccfg (get-in options [:config :clusters (keyword (:cluster options))])]
    (if (nil? ccfg)
      (throw (js/Error. (str "Cluster not defined: " (:cluster options))))
      ccfg)))

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

(S/defn ssh
  "SSH into a cluster node"
  [options :- schema/Options args]
  (require-arguments
    args
    (fn [node]
      (let [ccfg (cluster-config options)]
        (vagrant-cmd ccfg ["ssh" node])))))

(def cli
  {:doc "Run a cluster command"
   :options [["-x" "--cluster <name>" "Cluster name"
              :default config/default-cluster]]
   :commands {"create" create
              "destroy" destroy
              "up" up
              "down" down
              "status" status
              "ssh" ssh}})
