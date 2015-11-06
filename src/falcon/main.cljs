(ns falcon.main
  (:require
    [cljs.nodejs :as nodejs]
    [schema.core :as S]
    [falcon.config :as config]
    [falcon.core :as core]
    [falcon.cmd :as cmd :refer [commands]]
    [falcon.schema :as schema]))

(nodejs/enable-util-print!)

(def cli-options
  [["-f" "--config-file <file>" "Config file"
    :default config/default-file] 
   ["-e" "--environment <env>" "Environment"
    :default config/default-environment]
   ["-h" "--help" "Show this help"
    :default false]])

(defn -main [& args]
  (S/set-fn-validation! true)
  (cmd/exec! commands cli-options {} args))

(set! *main-cli-fn* -main)
