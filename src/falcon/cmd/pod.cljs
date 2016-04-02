(ns falcon.cmd.pod
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

(S/defn delete
  "Delete a pod"
  [opts args]
  (require-arguments
    args
    (fn [pod-name]
      (go 
        (<! (kubectl/run opts "delete" "pod" pod-name))))))

(S/defn list
  "List nodes"
  [opts args]
  (go 
    (<! (kubectl/run opts "get" "pods"))))

(S/defn logs
  "Get pod logs"
  [{:keys [follow] :as opts} args]
  (require-arguments
    args
    (fn [pod] 
      (go
        (let [kube-args (if follow ["-f" pod] [pod])]
          (<! (apply kubectl/run opts "logs" kube-args)))))))

(S/defn sh
  "Launch a shell in a pod"
  [opts args]
  (require-arguments
    args
    (fn [pod]
      (go 
        (<! (kubectl/run opts "exec" "-i" "--tty" pod "sh"))))))

(def cli
  {:doc "Pod commands"
   :options 
   [["-e" "--environment <env>" "Environment"]
    ["-f" "--follow" "Follow log tail"]]
   :commands {"delete" delete
              "list" list
              "logs" logs
              "sh" sh
              }})
