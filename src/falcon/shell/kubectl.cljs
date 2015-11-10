(ns falcon.shell.kubectl
  (:require
    [schema.core :as S]
    [falcon.config :as config]
    [falcon.shell :as core]))

(def Options
  {(S/optional-key :environment) S/Str
   (S/optional-key :kube-server) S/Str
   S/Keyword S/Any})

(defn- build-flags
  [opts]
  (let [ccfg (config/cluster opts)
        {:keys [environment kube-server]} (merge ccfg opts)] 
    (cond-> []
      (some? kube-server)
      (conj (str "--server=" kube-server))
      (some? environment)
      (conj (str "--namespace=" environment)))))

(S/defn run
  [options :- Options
   & args]
  (let [flags (build-flags options)]
    (core/passthru (concat ["kubectl"] flags args))))
