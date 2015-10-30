(ns falcon.main
  (:require
    [clojure.string :as string]
    [cljs.tools.cli :as cli]
    [cljs.nodejs :as nodejs]
    [schema.core :as S]
    [falcon.config :as config]
    [falcon.cluster :as cluster]
    [falcon.schema :as schema]))

(nodejs/enable-util-print!)

(def cli-options
  [["-c" "--config <file>" "Config file"
    :default "cloud/config.yml"]
   ["-e" "--environment <env>" "Environment"
    :default "local"]
   ["-h" "--help" "Show this help"
    :default false]
   ["-x" "--cluster <name>" "Cluster name"
    :default nil]])

(def ^:private commands
  {"cluster-create" {:function #'cluster/create}})

(defn- doc-string
  [fn-var]
  (let [doc (:doc (meta fn-var))]
    (string/trim (reduce str (drop 2 (string/split doc #"\n"))))))

(defn- print-usage
  [options-summary]
  (println "./falcon [options] command")
  (println options-summary)
  (println "Commands:")
  (doseq [[cmd {:keys [function]}] commands]
    (println "\t" cmd "\t" (doc-string function))))

(S/defn ^:private run-command
  [function {:keys [options] :as args} :- schema/Command]
  (let [cfg (config/read-yml (:config options))]
    (@function cfg args)))

(defn -main [& cli-args]
  (S/set-fn-validation! true)
  (let [{:keys [options arguments errors summary] :as args} (cli/parse-opts cli-args cli-options)
        {:keys [function]} (get commands (first arguments))]
    (cond 
      (some? errors) (println errors)
      (some? function) (run-command function args)
      :default (print-usage summary))))

(set! *main-cli-fn* -main)
