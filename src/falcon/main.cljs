(ns falcon.main
  (:require
    [cljs.nodejs :as nodejs]
    [schema.core :as S]
    [falcon.config :as config]
    [falcon.core :as core]
    [falcon.cmd :as cmd :refer-macros [ns-command]]
    [falcon.schema :as schema]))

(nodejs/enable-util-print!)

(def cli-options
  [["-f" "--config-file <file>" "Config file"
    :default "cloud/config.yml"] 
   ["-h" "--help" "Show this help"
    :default false]])

(def commands
  {"cluster" (cmd/exec-fn falcon.cmd.cluster.cli)}
  ;#_[#'cluster #'container #'controller #'deploy #'environment #'kube #'service]
  )

(defn -main [& args]
  (S/set-fn-validation! true)
  (cmd/exec! commands cli-options {} args))

(set! *main-cli-fn* -main)
