(ns falcon.shell.vagrant
  (:require
    [schema.core :as S]
    [falcon.core :as core :refer [map-keys species-path]]
    [falcon.schema :as schema]
    [falcon.shell :as shell]))

(defn- env-defs
  [ccfg]
  (core/map-keys name (:params ccfg)))

(defn- vagrant-dir
  []
  (str (.cwd js/process) "/" (species-path "cluster" "kubernetes-vagrant-coreos-cluster")))

(S/defn ^:private vagrant-options
  [ccfg :- schema/VagrantClusterConfig]
  {:cwd (vagrant-dir) 
   :env (env-defs ccfg)})

(defn run
  [ccfg cmd]
  (shell/passthru (concat ["vagrant"] cmd) (vagrant-options ccfg)))
