(ns falcon.shell.vagrant
  (:require
    [schema.core :as S]
    [falcon.schema :as schema]
    [falcon.shell :as shell]))

(defn- vagrant-dir
  []
  (str (.cwd js/process) "/cloud/cluster/kubernetes-vagrant-coreos-cluster"))

(S/defn ^:private vagrant-options
  [ccfg :- schema/VagrantClusterConfig]
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

(defn run
  [ccfg cmd]
  (shell/passthru (concat ["vagrant"] cmd) (vagrant-options ccfg)))
