(ns falcon.cmd.sa
  (:refer-clojure :exclude [list update])
  (:require
    [clojure.string :as string]
    [cljs.core.async :as async :refer [<!]]
    [cljs.pprint :refer [pprint]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.core :as core :refer [profiles species-path map-keys do-all-profiles]]
    [falcon.config :as config-ns]
    [falcon.schema :as schema]
    [falcon.shell :as shell]
    [falcon.shell.m4 :as m4]
    [falcon.shell.make :as make]
    [falcon.shell.kubectl :as kubectl]
    [falcon.template :refer [make-yml]])
  (:require-macros
    [falcon.core :refer [require-arguments]]
    [cljs.core.async.macros :refer [go]]))

(def ^:private yml-file "serviceaccount.yml")

(S/defn list
  "List service accounts"
  [opts args]
  (go
    (<! (kubectl/run opts "get" "serviceaccount"))))

(S/defn create
  "Create a service account"
  [opts args]
  (require-arguments 
    args
    (fn [service]
      (let [params {:service service}]
        (core/print-summary "Create service account:" opts params)
        (go
          (<! (make-yml yml-file opts params))
          (<! (-> (kubectl/run opts "create" "-f" (species-path service yml-file))
                  (shell/check-status))))))))

(S/defn delete
  "Delete a service account"
  [opts args]
  (require-arguments
    args
    (fn [service]
      (go
        (let [params {:service service}]
          (core/print-summary "Delete service account:" opts params)
          (when-not (:yes opts) (<! (core/safe-wait)))
          (<! (make-yml yml-file opts params))
          (<! (-> (kubectl/run opts "delete" "-f" (species-path service yml-file))
                  (shell/check-status))))))))

(def cli
  {:doc "Service account configuration"
   :options
   (core/cli-options
     [["-e" "--environment <env>" "Environment"]
      ["-p" "--profile <profile>" "Service profile"]
      ["-y" "--yes" "Skip safety prompts" :default false]])
   :commands
   {"create" create
    "delete" delete
    "list" list}})
