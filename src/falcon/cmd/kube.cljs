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
  [{:keys [environment] :as cfg} args]
  (go
    (<! (kubectl/run cfg "get" "pods"))))

(S/defn rc
  "Get replication controllers status"
  [{:keys [environment] :as cfg} args]
  (go
    (<! (kubectl/run cfg "get" "rc"))))

(S/defn services
  "Get services status"
  [{:keys [environment] :as cfg} args]
  (go
    (<! (kubectl/run cfg "get" "services"))))

(def cli
  {:doc "kubectl"
   :options [["-e" "--environment <env>" "Environment"
              :default "local"]
             ["-x" "--cluster <name>" "Cluster name"
              :default "main"]]
   :commands {"pods" pods
              "rc" rc}})
