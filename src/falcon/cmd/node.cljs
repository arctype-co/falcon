(ns falcon.cmd.node
  (:refer-clojure :exclude [list])
  (:require
    [clojure.string :as string]
    [cljs.core.async :as async :refer [<!]]
    [cljs.pprint :refer [pprint]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.schema :as schema]
    [falcon.shell :as shell]
    [falcon.shell.kubectl :as kubectl])
  (:require-macros
    [falcon.core :refer [require-arguments]]
    [cljs.core.async.macros :refer [go]]))

(defn- enable-disable-node
  [opts node enable?]
  (kubectl/run opts 
               "patch" "nodes" node "-p"
               (str "{\"spec\": {\"unschedulable\": " (not enable?) "}}")))

(S/defn disable
  "Disable scheduling new pods on a node"
  [opts args]
  (require-arguments
    args
    (fn [node]
      (go 
        (<! (enable-disable-node opts node false))))))

(S/defn enable
  "Enable scheduling new pods on a node"
  [opts args]
  (require-arguments
    args
    (fn [node]
      (go 
        (<! (enable-disable-node opts node true))))))

(S/defn list
  "List nodes"
  [opts args]
  (go 
    (<! (kubectl/run opts "get" "nodes"))))

(def cli
  {:doc "Integrated kubectl commands"
   :options 
   [["-e" "--environment <env>" "Environment"]]
   :commands {"disable" disable
              "enable" enable
              "list" list}})
