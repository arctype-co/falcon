(ns falcon.cmd.deploy
  (:require
    [clojure.string :as string]
    [cljs.core.async :as async :refer [<!]]
    [cljs.pprint :refer [pprint]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.config :as config]
    [falcon.core :as core]
    [falcon.schema :as schema]
    [falcon.cmd.container :as container-ns]
    [falcon.cmd.service :as service-ns])
  (:require-macros
    [falcon.core :refer [require-arguments]]
    [cljs.core.async.macros :refer [go]]))

(S/defn create
  "Build and push container, then deploy replication controller."
  [opts args]
  (require-arguments
    (rest args)
    (fn [service]
      (go
        (let [container-tag (<! (container-ns/build opts [nil service]))]
          (<! (container-ns/push (assoc opts :tag container-tag) [nil service]))
          (<! (service-ns/create-rc opts [nil service container-tag])))))))

(S/defn roll
  "Build and push container, then rolling deploy it's replication controller."
  [opts args]
  (require-arguments
    (rest args)
    (fn [service old-controller-tag]
      (go
        (let [container-tag (<! (container-ns/build opts [nil service]))]
          (<! (container-ns/push (assoc opts :tag container-tag) [nil service]))
          (<! (service-ns/rolling-update opts [nil service old-controller-tag container-tag])))))))

(def cli
  {:doc "High-level deployment commands"
   :options [["-e" "--environment <env>" "Environment"
              :default config/default-environment]
             ["-x" "--cluster <name>" "Cluster name"
              :default config/default-cluster]
             ["-n" "--no-cache" "Disable docker cache"
              :default false]]
   :commands {"create" create
              "roll" roll}})
