(ns falcon.kube
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

(def ^:private cli-options
  [["-e" "--environment <env>" "Environment"
    :default "local"]
   ["-x" "--cluster <name>" "Cluster name"
    :default "main"]])

(S/defn pods
  "Get pods status"
  [{:keys [environment] :as cfg} args]
  (go
    (<! (kubectl/run cfg "get" "pods"))))

(S/defn command
  "Run a kube command"
  [function
   config :- schema/Config
   {:keys [arguments]} :- schema/Command]
  (let [{:keys [arguments options errors summary]} (cli/parse-opts arguments cli-options)
        {:keys [environment cluster]} options]
    (cond
      (some? errors) 
      (println errors)

      (= "help" (second arguments))
      (println summary)

      :default (function options (vec (rest arguments))))))
