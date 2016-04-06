(ns falcon.cmd.deploy
  (:refer-clojure :exclude [update])
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
    [falcon.cmd.rc :as rc])
  (:require-macros
    [falcon.core :refer [require-arguments]]
    [cljs.core.async.macros :refer [go]]))

(S/defn create
  "Build and push container, then deploy replication controller."
  [{:keys [container-tag]:as opts} args]
  (require-arguments
    args
    (fn [service]
      (let [params {:service service}]
        (core/print-summary "Create deployment:" opts params)
        (go
          (let [container-tag (or container-tag (<! (container-ns/build opts [service])))
                opts (assoc opts :container-tag container-tag)]
            (<! (container-ns/push opts [service]))
            (<! (rc/create opts [service]))))))))

(S/defn update
  "Build and push container, remove current controller tag, and redeploy replication controller."
  [{:keys [container-tag]:as opts} args]
  (require-arguments
    args
    (fn [service old-controller-tag]
      (let [params {:service service}]
        (core/print-summary "Update deployment:" opts params)
        (go
          (let [container-tag (or container-tag (<! (container-ns/build opts [service])))
                opts (assoc opts :container-tag container-tag)]
            (<! (container-ns/push opts [service]))
            (<! (rc/delete opts [service old-controller-tag]))
            (<! (rc/create opts [service]))))))))

(S/defn roll
  "Build and push container, then rolling deploy it's replication controller."
  [{:keys [container-tag] :as opts} args]
  (require-arguments
    args
    (fn [service old-controller-tag]
      (let [params {:service service
                    :old-controller-tag old-controller-tag}]
        (core/print-summary "Rolling deployment:" opts params)
        (go
          (let [container-tag (or container-tag (<! (container-ns/build opts [service])))
                opts (assoc opts :container-tag container-tag)]
            (<! (container-ns/push opts [service]))
            (<! (rc/roll opts [service old-controller-tag container-tag]))))))))

(def cli
  {:doc "High-level deployment commands"
   :options [["-t" "--git-tag <tag>" "Git tag"]
             ["-c" "--container-tag <tag>" "Container tag"]
             ["-n" "--no-cache" "Disable docker cache"
              :default false]]
   :commands {"create" create
              "update" update
              "roll" roll}})
