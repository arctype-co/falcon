(ns falcon.cmd.environment
  (:require
    [clojure.string :as string]
    [cljs.core.async :as async :refer [<!]]
    [cljs.pprint :refer [pprint]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.core :as core :refer [cloud-path]]
    [falcon.schema :as schema]
    [falcon.shell :as shell]
    [falcon.shell.kubectl :as kubectl]
    [falcon.shell.m4 :as m4])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(S/defn create
  "Create a new environment (namespace)"
  [opts :- schema/Options args]
  (println opts)
  (core/print-summary "Create namespace:" opts {})
  (go 
    (<! (m4/write (m4/defs opts)
                  [(cloud-path "environment" "namespace.yml.m4")]
                  (cloud-path "environment" "namespace.yml")))
    (<! (kubectl/run opts "create" "-f" (cloud-path "environment" "namespace.yml")))))

(S/defn delete
  "Delete an environment (namespace)"
  [opts :- schema/Options args]
  (core/print-summary "Deleting namespace:" opts {})
  (go 
    (<! (core/safe-wait))
    (<! (m4/write (m4/defs opts)
                  [(cloud-path "environment" "namespace.yml.m4")]
                  (cloud-path "environment" "namespace.yml")))
    (<! (kubectl/run opts "delete" "-f" (cloud-path "environment" "namespace.yml")))))

(def cli
  {:doc "Environment setup"
   :options []
   :commands {"create" create
              "delete" delete}})
