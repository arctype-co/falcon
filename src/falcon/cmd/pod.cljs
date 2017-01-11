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

(S/defn describe
  "Describe a pod"
  [opts args]
  (require-arguments
    args
    (fn [pod-name]
      (go 
        (<! (kubectl/run opts "describe" "pod" pod-name))))))

(S/defn ip
  "Show IP address of a pod"
  [opts args]
  (require-arguments
    args
    (fn [pod-name]
      (go 
        (<! (kubectl/run opts "get" "-o" "jsonpath={.status.podIP}" "pod" pod-name))
        (println)))))

(S/defn list
  "List nodes"
  [opts args]
  (go 
    (<! (kubectl/run opts "get" "pods" "-o" "wide"))))

(S/defn logs
  "Get pod logs"
  [{:keys [follow] :as opts} args]
  (go
    (let [pod (first args)
          pod-container (second args)
          kube-args (if follow 
                      ["-f" pod]
                      [pod])
          kube-args (if (some? pod-container)
                      (conj kube-args pod-container)
                      kube-args)]
      (<! (apply kubectl/run opts "logs" kube-args)))))

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
              "describe" describe
              "ip" ip
              "list" list
              "logs" logs
              "sh" sh
              }})
