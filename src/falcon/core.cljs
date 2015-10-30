(ns falcon.core
  (:require
    [clojure.string :as string]
    [cljs.tools.cli :as cli]
    [cljs.nodejs :as nodejs]
    [schema.core :as S]
    [falcon.cluster :as cluster]
    [falcon.schema :as schema]))

(nodejs/enable-util-print!)

(def cli-options
  [["-c" "--config <file>" "Config file"
    :default "cloud/config.yml"]
   ["-e" "--environment <env>" "Environment"
    :default "local"]
   ["-h" "--help" "Show this help"]
   #_["-x" "--cluster <name>" "Cluster name"]])

(def ^:private commands
  {"create-cluster" {:fn #'cluster/create}})

(defn- doc-string
  [fn-var]
  (let [doc (:doc (meta fn-var))]
    (string/trim (reduce str (drop 2 (string/split doc #"\n"))))))

(defn- print-usage
  [options-summary]
  (println "./falcon [options] command")
  (println options-summary)
  (println "Commands:")
  (doseq [[cmd {:keys [fn]}] commands]
    (println "\t" cmd "\t" (doc-string fn))))

(defn -main [& args]
  (let [{:keys [options arguments errors summary] :as args} (cli/parse-opts args cli-options)
        cmd-fn (get commands (first arguments))]
    (cond 
      (some? errors) (println errors)
      (some? cmd-fn) (@cmd-fn args)
      :default (print-usage summary))))

(set! cljs.core/*main-cli-fn* -main)
