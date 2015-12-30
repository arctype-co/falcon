(ns falcon.cmd.kube
  (:refer-clojure :exclude [do])
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
        (<! (kubectl/run opts "exec" "-i" "--tty" pod "bash"))))))

(S/defn delete-pod
  "Delete a pod"
  [opts args]
  (require-arguments
    args
    (fn [pod]
      (go 
        (<! (kubectl/run opts "delete" "pod" pod))))))

(S/defn do
  "Do a kubernetes command"
  [opts args]
  (go (<! (apply kubectl/run opts args))))

(def cli
  {:doc "Integrated kubectl commands"
   :options []
   :commands {"do" do
              "env" env
              "logs" logs
              "nodes" nodes
              "pods" pods
              "delete-pod" delete-pod
              "rc" rc
              "sh" sh
              "services" services}})
