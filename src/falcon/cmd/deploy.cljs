(ns falcon.cmd.deploy
  (:require
    [clojure.string :as string]
    [cljs.core.async :as async :refer [<!]]
    [cljs.pprint :refer [pprint]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.config :as config]
    [falcon.core :as core :refer [profiles species-path map-keys do-all-profiles]]
    [falcon.schema :as schema]
    [falcon.cmd.container :as container-ns]
    [falcon.cmd.rc :as rc]
    [falcon.shell :as shell]
    [falcon.shell.m4 :as m4]
    [falcon.shell.make :as make]
    [falcon.shell.kubectl :as kubectl]
    [falcon.template :refer [make-yml]])
  (:require-macros
    [falcon.core :refer [require-arguments]]
    [cljs.core.async.macros :refer [go]]))

(def ^:private yml-file "deployment.yml")

(defn- deployment-name
  [{:keys [profile]} service]
  (str "deployment/" service
       (when (some? profile) (str "-" profile))))
  

(S/defn create
  "Create a Kubernetes deployment."
  [opts args]
  (require-arguments
    args
    (fn [service]
      (do-all-profiles opts (profiles opts service)
                       (fn [opts]
                         (let [{:keys [container-tag]} (config/service opts service)
                               params {:service service
                                       :container-tag container-tag}]
                           (core/print-summary "Create deployment:" opts params)
                           (go
                             (<! (make-yml yml-file opts params))
                             (<! (-> (kubectl/run opts "create" "-f" (species-path service yml-file))
                                     (shell/check-status))))))))))

(S/defn delete
  "Delete a Kubernetes deployment."
  [opts args]
  (require-arguments
    args
    (fn [service]
        (do-all-profiles opts (profiles opts service)
          (fn [opts]
            (let [{:keys [container-tag]} (config/service opts service)
                  params {:service service
                          :container-tag container-tag}]
              (core/print-summary "Delete deployment:" opts params)
              (go
                (<! (-> (kubectl/run opts "delete" (deployment-name opts service))
                        (shell/check-status))))))))))

(S/defn update-deployment
  "Apply a deployment"
  [opts args]
  (require-arguments
    args
    (fn [service]
      (do-all-profiles opts (profiles opts service)
                       (fn [opts]
                         (let [{:keys [container-tag]} (config/service opts service)
                               params {:service service
                                       :container-tag container-tag}]
                           (core/print-summary "Apply deployment:" opts params)
                           (go
                             (<! (make-yml yml-file opts params))
                             (<! (-> (kubectl/run opts "apply" "-f" (species-path service yml-file))
                                     (shell/check-status))))))))))

(S/defn list-deployments
  "List deployments"
  [opts args]
  (go
    (<! (kubectl/run opts "get" "deployment"))))

(S/defn status
  "Get deployment status"
  [{:keys [profile] :as opts} args]
  (require-arguments
   args
   (fn [service]
     (go
       (<! (kubectl/run opts "rollout" "status" (deployment-name opts service)))))))

(S/defn history
  "Get deployment history"
  [{:keys [profile] :as opts} args]
  (require-arguments
    args
    (fn [service]
      (go
        (<! (kubectl/run opts "rollout" "history" (deployment-name opts service)))))))

(def cli
  {:doc "High-level deployment commands"
   :options (core/cli-options
              [["-c" "--container-tag <tag>" "Container tag"]
               ["-p" "--profile <profile>" "Service profile"]])
   :commands {"create" create
              "delete" delete
              "update" update-deployment
              "list" list-deployments
              "status" status
              "history" history}})
