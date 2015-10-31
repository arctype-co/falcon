
(ns falcon.deploy
  (:require
    [clojure.string :as string]
    [cljs.core.async :as async :refer [<!]]
    [cljs.pprint :refer [pprint]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.core :as core]
    [falcon.schema :as schema]
    [falcon.container :as container-ns]
    [falcon.service :as service-ns])
  (:require-macros
    [falcon.core :refer [require-arguments]]
    [cljs.core.async.macros :refer [go]]))

(def ^:private cli-options
  [["-e" "--environment <env>" "Environment"
    :default "local"]
   ["-x" "--cluster <name>" "Cluster name"
    :default "main"]
   ["-s" "--service <name>" "Service name"]
   ["-n" "--no-cache" "Disable docker cache"
    :default false]])

(S/defn deploy
  "Build and push container, then deploy replication controller."
  [{:keys [environment service no-cache] :as cfg} args]
  (go
    (let [container-tag (<! (container-ns/build {:container service
                                                 :no-cache no-cache}
                                                []))]
      (<! (container-ns/push {:container service
                              :tag container-tag}
                             []))
      (<! (service-ns/create-rc
            {:environment environment
             :service service}
            [container-tag])))))

(S/defn rolling-deploy
  "Build an push container, then rolling deploy the replication controller."
  [{:keys [environment service no-cache] :as cfg} args]
  (require-arguments
    args
    (fn [old-controller-tag]
      (go
        (let [container-tag (<! (container-ns/build {:container service
                                                     :no-cache no-cache}
                                                    []))]
          (<! (container-ns/push {:container service
                                  :tag container-tag}
                                 []))
          (<! (service-ns/rolling-update
                {:environment environment
                 :service service}
                [old-controller-tag container-tag])))))))

(S/defn command
  "Run a deployment command"
  [function
   config :- schema/Config
   {:keys [arguments]} :- schema/Command]
  (let [{:keys [arguments options errors summary]} (cli/parse-opts arguments cli-options)
        {:keys [service]} options]
    (cond
      (some? errors) 
      (println errors)

      (some? service)
      (function options (vec (rest arguments)))

      :default (println summary))))
