(ns falcon.cmd.cluster
  (:require 
    [cljs.pprint :refer [pprint]]
    [cljs.core.async :as async :refer [<!]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.config :as config]
    [falcon.core :as core :refer [species-path] :refer-macros [require-arguments]]
    [falcon.schema :as schema]
    [falcon.shell :as shell]
    [falcon.shell.kubectl :as kubectl]
    [falcon.shell.vagrant :as vagrant])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(defn- env-defs
  [ccfg]
  (core/map-keys name (:params ccfg)))

(defmulti do-create :provider)

(defmethod do-create :default
  [ccfg options]
  (core/print-summary "About to create cluster:" options ccfg)
  (let [install-env (env-defs ccfg)]
    (go (<! (core/safe-wait))
        (<! (-> (shell/passthru 
                  ["./kube-up.sh"]
                  {:cwd (species-path "cluster" "kubernetes/cluster")
                   :env install-env})
                shell/check-status)))))

(defmethod do-create "vagrant"
  [ccfg]
  (println "Creating cluster with configuration:")
  (pprint ccfg)
  (vagrant/run ccfg ["up"]))

(S/defn create
  "Create a new cluster"
  [options :- schema/Options args]
  (do-create (config/cluster options)))

(defmulti do-halt :provider)

(defmethod do-halt :default
  [ccfg]
  (throw (js/Error. "Operation not supported")))

(defmethod do-halt "vagrant"
  [ccfg]
  (println "Bringing cluster offline:")
  (pprint ccfg)
  (go (<! (core/safe-wait))
      (<! (vagrant/run ccfg ["halt"]))))

(S/defn halt
  "Bring a cluster offline"
  [options :- schema/Options args]
  (do-halt (config/cluster options)))

(defmulti do-destroy
  (fn [ccfg options] (:provider ccfg)))

(defmethod do-destroy :default
  [{:keys [install-env] :as ccfg} options]
  (core/print-summary "About to DESTROY cluster:" options ccfg)
  (let [install-env (core/map-keys name install-env)]
    (go (<! (core/safe-wait))
        (<! (-> (shell/passthru 
                  ["./kube-down.sh"]
                  {:cwd (species-path "cluster" "kubernetes/cluster")
                   :env install-env})
                shell/check-status)))))

(defmethod do-destroy "vagrant"
  [ccfg options]
  (core/print-summary "About to DESTROY cluster:" options ccfg)
  (go (<! (core/safe-wait))
      (<! (-> (vagrant/run ccfg ["destroy"])))))

(S/defn destroy
  "Destroy a cluster"
  [options :- schema/Options args]
  (do-destroy (config/cluster options) options))

(defmulti do-status (fn [ccfg options] (:provider ccfg)))

(defmethod do-status :default
  [ccfg opts]
  (go (<! (kubectl/run opts "get" "nodes"))))

(defmethod do-status "vagrant"
  [ccfg opts]
  (go (<! (vagrant/run ccfg ["status"]))
      (<! (kubectl/run opts "get" "nodes"))))

(S/defn status
  "Print cluster status"
  [options :- schema/Options args]
  (do-status (config/cluster options) options))

(defmulti do-ssh (fn [ccfg node] (:provider ccfg)))

(defmethod do-ssh :default
  [ccfg node]
  (throw (js/Error. "Operation not supported")))

(defmethod do-ssh "vagrant"
  [ccfg node]
  (vagrant/run ccfg ["ssh" node]))

(S/defn ssh
  "SSH into a cluster node"
  [options :- schema/Options args]
  (require-arguments
    args
    (fn [node]
      (do-ssh (config/cluster options) node))))

(def cli
  {:doc "Run a cluster command"
   :commands {"create" create
              "destroy" destroy
              "halt" halt
              "status" status
              "ssh" ssh}})
