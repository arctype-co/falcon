(ns falcon.shell.kubectl
  (:require
    [schema.core :as S]
    [falcon.shell :as core]))

(def Options
  {:environment S/Str
   S/Keyword S/Any})

(defn- build-flags
  [{:keys [environment]}]
  [(str "--namespace=" environment)])

(S/defn run
  [options :- Options
   & args]
  (let [flags (build-flags options)]
    (core/passthru (concat ["kubectl"] flags args))))
