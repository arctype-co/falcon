(ns falcon.shell.kubectl
  (:require
    [schema.core :as S]
    [falcon.config :as config]
    [falcon.shell :as core]))

(def Options
  {(S/optional-key :environment) S/Str
   (S/optional-key :kubeconfig) S/Str
   (S/optional-key :kube-server) S/Str
   (S/optional-key :certificate-authority) S/Str
   (S/optional-key :client-certificate) S/Str
   (S/optional-key :client-key) S/Str
   (S/optional-key :shell-mode) (S/enum :passthru :spawn)
   S/Keyword S/Any})

(defn- build-flags
  [opts]
  (let [ccfg (config/cluster opts)
        {:keys [environment kubeconfig kube-server certificate-authority client-certificate client-key]} (merge ccfg opts)] 
    (cond-> []
      (some? kubeconfig)
      (conj (str "--kubeconfig=" kubeconfig))
      (some? kube-server)
      (conj (str "--server=" kube-server))
      (some? certificate-authority)
      (conj (str "--certificate-authority=" certificate-authority))
      (some? client-certificate)
      (conj (str "--client-certificate=" client-certificate))
      (some? client-key)
      (conj (str "--client-key=" client-key))
      (some? environment)
      (conj (str "--namespace=" environment)))))

(S/defn run
  [options :- Options
   & args]
  (let [flags (build-flags options)]
    (case (:shell-mode options)
      :spawn (core/spawn (concat ["kubectl"] flags args) {})
      ;:passthru
      (core/passthru (concat ["kubectl"] flags args)))))
