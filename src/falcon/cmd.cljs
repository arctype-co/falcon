(ns falcon.cmd
  (:require
    [clojure.string :as string]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.config :as config]
    [falcon.schema :as schema]
    [falcon.cmd.cluster :as cluster]
    [falcon.cmd.config :as config-cmd]
    [falcon.cmd.container :as container]
    [falcon.cmd.deploy :as deploy]
    [falcon.cmd.ds :as ds]
    [falcon.cmd.environment :as environment]
    [falcon.cmd.job :as job]
    [falcon.cmd.kube :as kube]
    [falcon.cmd.node :as node]
    [falcon.cmd.pod :as pod]
    [falcon.cmd.rc :as rc]
    [falcon.cmd.ss :as ss]
    [falcon.cmd.secret :as secret]
    [falcon.cmd.service :as service]))

(defn- doc-string
  [fn-var]
  (let [doc (:doc (meta fn-var))
        schema-doc (string/trim (reduce str (drop 2 (string/split doc #"\n"))))]
    (if (empty? schema-doc)
      (if (nil? doc) "" doc)
      schema-doc)))

(defn- print-usage
  [options-summary commands invoked-args]
  (println options-summary)
  (println "Commands:")
  (doseq [[cmd-str cmd-fn] commands]
    (println "\t" cmd-str "\t" (doc-string cmd-fn))))

(defn- initial-conf
  [options]
  (let [conf (config/read-yml (:config-file options))]
    (merge {:config conf} (:options conf))))

(S/defn ^:private launch!
  [command-fn :- S/Any
   options :- schema/Options
   stack-options :- schema/Options
   args :- [S/Str]]
  (try 
    ; load config once
    (let [stack-options (if (empty? stack-options) (initial-conf options) 
                          stack-options)
          options (merge stack-options options)]
      (command-fn options args))
    (catch js/Error e
      (throw e))))

(S/defn exec!
  [cli-commands :- {S/Str S/Any}
   cli-options :- (S/maybe [[S/Any]])
   stack-options :- schema/Options
   cli-args :- (S/maybe [S/Str])]
  (let [{:keys [options arguments errors summary]} (cli/parse-opts cli-args cli-options :in-order true)
        command-fn (get cli-commands (first arguments))]
    (cond 
      (some? errors) (println errors)
      (some? command-fn) (launch! command-fn options stack-options (vec (rest arguments)))
      :default (print-usage summary cli-commands arguments))))

(defn- cli-exec
  [{:keys [doc options commands]}]
  (with-meta
    (fn [stack-options args]
      (exec! commands options stack-options args))
    {:doc doc}))

(def commands
  {"cluster" (cli-exec cluster/cli)
   "config" (cli-exec config-cmd/cli)
   "container" (cli-exec container/cli)
   "deploy" (cli-exec deploy/cli)
   "ds" (cli-exec ds/cli)
   "env" (cli-exec environment/cli)
   "job" (cli-exec job/cli)
   "node" (cli-exec node/cli)
   "pod" (cli-exec pod/cli)
   "rc" (cli-exec rc/cli)
   "ss" (cli-exec ss/cli)
   "secret" (cli-exec secret/cli)
   "service" (cli-exec service/cli)
   "kube" kube/do})
