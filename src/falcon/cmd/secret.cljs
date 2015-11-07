(ns falcon.cmd.secret
  (:require
    [cljs.core.async :as async :refer [<!]]
    [cljs.pprint :refer [pprint]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.core :as core :refer [cloud-path]]
    [falcon.schema :as schema]
    [falcon.shell :as shell]
    [falcon.shell.m4 :as m4]
    [falcon.shell.kubectl :as kubectl])
  (:require-macros
    [falcon.core :refer [require-arguments]]
    [cljs.core.async.macros :refer [go]]))

(defn- m4-defs
  [{:keys [config] :as opts} {:keys [secret]}]
  (merge (m4/defs opts)
         {"SECRET" secret
          "LOGGLY_TOKEN_BASE64" (core/base64 (get-in config [:loggly :token]))}))

(defn- make-yml
  [yml-name opts {:keys [secret] :as params}]
  (let []
    (-> (m4/write (m4-defs opts params)
                  [(cloud-path secret (str yml-name ".m4"))]
                  (cloud-path secret yml-name))
        (shell/check-status))))

(S/defn list-secrets
  "List secrets"
  [opts :- schema/Options
   args]
  (go (<! (kubectl/run opts "get" "secrets"))))

(S/defn create
  "Load a secret config"
  [opts :- schema/Options
   args]
  (require-arguments
    args
    (fn [secret]
      (let [params {:secret secret}]
        (core/print-summary "Create secret" opts params)
        (go
          (<! (make-yml "secret.yml" opts params))
          (<! (kubectl/run opts "create" "-f" (cloud-path secret "secret.yml"))))))))

(S/defn delete 
  "Unload a secret config"
  [opts :- schema/Options
   args]
  (require-arguments
    args
    (fn [secret]
      (let [params {:secret secret}]
        (core/print-summary "Delete secret" opts params)
        (go
          (<! (core/safe-wait))
          (<! (make-yml "secret.yml" opts params))
          (<! (kubectl/run opts "delete" "-f" (cloud-path secret "secret.yml"))))))))

(def cli
  {:doc "Secret configuration and deployment"
   :options
   []
   :commands
   {"create" create
    "delete" delete
    "list" list-secrets}})
