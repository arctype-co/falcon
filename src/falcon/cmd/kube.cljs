(ns falcon.cmd.kube
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

(S/defn pods
  "Get pods status"
  [{:keys [environment] :as opts} args]
  (go
    (<! (kubectl/run opts "get" "pods"))))

(S/defn rc
  "Get replication controllers status"
  [{:keys [environment] :as opts} args]
  (go
    (<! (kubectl/run opts "get" "rc"))))

(S/defn services
  "Get services status"
  [{:keys [environment] :as opts} args]
  (go
    (<! (kubectl/run opts "get" "services"))))

(def cli
  {:doc "kubectl"
   :options []
   :commands {"pods" pods
              "rc" rc}})
