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

(S/defn env
  "Get environments"
  [opts args]
  (go
    (<! (kubectl/run opts "get" "namespaces"))))

(S/defn logs
  "Get logs"
  [opts args]
  (require-arguments
    args
    (fn [node] (go
                 (<! (kubectl/run opts "logs" node))))))

(S/defn nodes
  "Get nodes status"
  [opts args]
  (go
    (<! (kubectl/run opts "get" "nodes"))))

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

(S/defn sh
  "Launch a shell in a pod"
  [opts args]
  (require-arguments
    args
    (fn [pod]
      (go 
        (<! (kubectl/run opts "exec" "-i" "--tty" "-p" pod "bash"))))))

(def cli
  {:doc "Integrated kubectl commands"
   :options []
   :commands {"env" env
              "logs" logs
              "nodes" nodes
              "pods" pods
              "rc" rc
              "sh" sh
              "services" services}})
