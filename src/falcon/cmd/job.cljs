(ns falcon.cmd.job
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

(S/defn create
  "Create a job"
  [opts args]
  (require-arguments
    args
    (fn [pod-name]
      (go 
        (<! (kubectl/run opts "create" "job" pod-name))))))

(S/defn list
  "List jobs"
  [opts args]
  (go 
    (<! (kubectl/run opts "get" "jobs"))))

(def cli
  {:doc "Job commands"
   :options 
   [["-e" "--environment <env>" "Environment"]]
   :commands {"create" create
              "list" list}})
