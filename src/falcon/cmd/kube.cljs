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

(S/defn logs
  "Get logs"
  [opts args]
  (require-arguments
    args
    (fn [node] (go
                 (<! (kubectl/run opts "logs" node))))))

(S/defn pods
  "Get pods status"
  [opts args]
  (go
    (<! (kubectl/run opts "get" "pods"))))

(S/defn rc
  "Get replication controllers status"
  [opts args]
  (go
    (<! (kubectl/run opts "get" "rc"))))

(S/defn services
  "Get services status"
  [opts args]
  (go
    (<! (kubectl/run opts "get" "services"))))

(def cli
  {:doc "kubectl"
   :options []
   :commands {"logs" logs
              "pods" pods
              "rc" rc
              "services" services}})
