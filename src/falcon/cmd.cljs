(ns falcon.cmd
  (:require
    [clojure.string :as string]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.config :as config]
    [falcon.schema :as schema]
    [falcon.cmd.cluster :as cluster]
    ;[falcon.cmd.container :as container]
    ;[falcon.cmd.deploy :as deploy]
    ;[falcon.cmd.environment :as environment]
    ;[falcon.cmd.kube :as kube]
    ;[falcon.cmd.service :as service]
    ))

(defn- doc-string
  [fn-var]
  (let [doc (:doc (meta fn-var))]
    (string/trim (reduce str (drop 2 (string/split doc #"\n"))))))

(defn- print-usage
  [options-summary commands invoked-args]
  (println options-summary)
  (println "Commands:")
  (doseq [[cmd-str cmd-fn] commands]
    (println "\t" cmd-str "\t" (doc-string cmd-fn))))

(S/defn ^:private launch!
  [command-fn :- S/Any
   options :- schema/Options
   stack-options :- schema/Options
   args :- [S/Str]]
  (try 
    ; load config once
    (let [stack-options (if (empty? stack-options)
                          {:config (config/read-yml (:config-file options))}
                          stack-options)]
      (command-fn (merge stack-options options) args))
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
      (some? command-fn) (launch! command-fn options stack-options arguments)
      :default (print-usage summary cli-commands arguments))))

(defn exec-fn
  [{:keys [doc options commands]}]
  (fn [stack-options args]
    (exec! commands options stack-options (vec (rest args)))))
