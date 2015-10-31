(ns falcon.main
  (:require
    [clojure.string :as string]
    [cljs.tools.cli :as cli]
    [cljs.nodejs :as nodejs]
    [schema.core :as S]
    [falcon.cluster :as cluster]
    [falcon.config :as config]
    [falcon.container :as container]
    [falcon.environment :as environment]
    [falcon.schema :as schema]
    [falcon.service :as service]))

(nodejs/enable-util-print!)

(def cli-options
  [["-c" "--config <file>" "Config file"
    :default "cloud/config.yml"] 
   ["-h" "--help" "Show this help"
    :default false]])

(def ^:private commands
  {"cluster-create" {:launch #'cluster/command
                     :function #'cluster/create}
   "cluster-destroy" {:launch #'cluster/command
                      :function #'cluster/destroy}
   "cluster-status" {:launch #'cluster/command
                     :function #'cluster/status}

   "container-build" {:launch #'container/command
                      :function #'container/build}
   "container-push" {:launch #'container/command
                     :function #'container/push}

   "service-create" {:launch #'service/command
                     :function #'service/create}
   "service-delete" {:launch #'service/command
                     :function #'service/delete}
   "service-deploy" {:launch #'service/command
                     :function #'service/deploy}

   "environment-create" {:launch #'environment/command
                         :function #'environment/create}})

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

(defn- default-launch
  [function cfg args]
  (function cfg args))

(S/defn ^:private run-command
  [{:keys [launch function]}
   {:keys [options] :as args} :- schema/Command]
  (try 
    (let [launch (or launch #'default-launch)
          cfg (config/read-yml (:config options))]
      (@launch @function cfg args))
    (catch js/Error e
      (throw e))))

(defn -main [& cli-args]
  (S/set-fn-validation! true)
  (let [{:keys [options arguments errors summary] :as args} (cli/parse-opts cli-args cli-options :in-order true)
        command (get commands (first arguments))]
    (cond 
      (some? errors) (println errors)
      (some? command) (run-command command args)
      :default (print-usage summary))))

(set! *main-cli-fn* -main)
