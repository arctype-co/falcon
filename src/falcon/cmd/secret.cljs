(ns falcon.cmd.secret
  (:refer-clojure :exclude [update])
  (:require
    [cljs.core.async :as async :refer [<!]]
    [cljs.pprint :refer [pprint]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.core :as core :refer [species-path]]
    [falcon.config :as config-ns]
    [falcon.schema :as schema]
    [falcon.shell :as shell]
    [falcon.shell.m4 :as m4]
    [falcon.shell.make :as make]
    [falcon.shell.kubectl :as kubectl])
  (:require-macros
    [falcon.core :refer [require-arguments]]
    [cljs.core.async.macros :refer [go]]))

(defn- env-file-name
  "Name an environment variable for a file"
  [file-name]
  (->
    (-> file-name 
        (.replace (js/RegExp. "\\." "g") "_")
        (.replace (js/RegExp. "\\-" "g") "_"))
    (str "_base64")
    (.toUpperCase)))

(defn- encode-secret-files
  [secret secret-file-names]
  (into {} (map
             (fn [secret-file-name]
               [(env-file-name secret-file-name)
                (core/base64 (core/read-file-buffer (species-path secret secret-file-name)))])
             secret-file-names)))

(defn- secret-m4-defs
  [{:keys [config] :as opts} {:keys [service] :as params}]
  (let [svc-config (config-ns/service opts service)
        secret-files (map name (:secret-files svc-config))]
    (merge (m4/defs opts params)
           (encode-secret-files service secret-files))))

(defn- make-yml
  [yml-name opts {:keys [service] :as params}]
  (let []
    (-> (m4/write (secret-m4-defs opts params)
                  [(species-path service (str yml-name ".m4"))]
                  (species-path service yml-name))
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
      (let [params {:service secret}
            defs (m4/defs opts params)]
        (core/print-summary "Create secret" opts params)
        (go
          ; Run a Makefile if there is one
          (<! (make/run defs ["-C" (species-path secret) "secret"]))
          (<! (make-yml "secret.yml" opts params))
          (<! (kubectl/run opts "create" "-f" (species-path secret "secret.yml"))))))))

(S/defn update
  "Replace a secret config"
  [opts :- schema/Options
   args]
  (require-arguments
    args
    (fn [secret]
      (let [params {:service secret}
            defs (m4/defs opts params)]
        (core/print-summary "Update secret" opts params)
        (go
          ; Run a Makefile if there is one
          (<! (make/run defs ["-C" (species-path secret) "secret"]))
          (<! (make-yml "secret.yml" opts params))
          (<! (kubectl/run opts "update" "-f" (species-path secret "secret.yml"))))))))

(S/defn delete 
  "Unload a secret config"
  [opts :- schema/Options
   args]
  (require-arguments
    args
    (fn [secret]
      (let [params {:service secret}]
        (core/print-summary "Delete secret" opts params)
        (go
          (<! (core/safe-wait))
          (<! (make-yml "secret.yml" opts params))
          (<! (kubectl/run opts "delete" "-f" (species-path secret "secret.yml"))))))))

(def cli
  {:doc "Secret configuration and deployment"
   :options [["-e" "--environment <env>" "Environment"]]
   :commands
   {"create" create
    "delete" delete
    "update" update
    "list" list-secrets}})
