(ns falcon.cmd.job
  (:refer-clojure :exclude [list])
  (:require
    [cljs.core.async :as async :refer [<!]]
    [cljs.pprint :refer [pprint]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.core :as core]
    [falcon.config :as config-ns]
    [falcon.schema :as schema]
    [falcon.shell :as shell]
    [falcon.shell.kubectl :as kubectl]
    [falcon.template :as template])
  (:require-macros
    [falcon.core :refer [require-arguments]]
    [cljs.core.async.macros :refer [go]]))

(S/defn create
  "Create a job"
  [opts args]
  (let [service (first args)
        job-args (rest args)
        controller-tag (core/new-tag)]
    (when (nil? service)
      (throw (js/Error. "Expected arguments: [service & job-args]")))
    (core/do-all-profiles
      opts
      (core/profiles opts service)
      (fn [opts]
        (let [{:keys [container-tag]} (config-ns/service opts service)
              container-tag (or (:container-tag opts) container-tag)
              params {:arguments (template/print-args job-args)
                      :service service
                      :controller-tag controller-tag
                      :container-tag container-tag}]
          (core/print-summary "Create job:" opts params)
          (go
            (<! (template/make-yml "job.yml" opts params))
            (<! (-> (kubectl/run opts "create" "-f" (core/species-path service "job.yml"))
                    (shell/check-status)))))))))

(S/defn delete
  "Cancel a job. Buggy - goes into infinite loop when job is failed."
  [opts args]
  (require-arguments 
    args
    (fn [service controller-tag]
      (core/do-all-profiles opts (core/profiles opts service)
        (fn [opts]
          (let [{:keys [container-tag]} (config-ns/service opts service)
                container-tag (or (:container-tag opts) container-tag)
                params {:service service
                        :controller-tag controller-tag
                        :container-tag container-tag}]
            (core/print-summary "Delete job:" opts params)
            (go
              (when-not (:yes opts) (<! (core/safe-wait)))
              (<! (template/make-yml "job.yml" opts params))
              (<! (kubectl/run opts "delete" "-f" (core/species-path service "job.yml"))))))))))

(S/defn list
  "List jobs"
  [opts args]
  (go 
    (<! (kubectl/run opts "get" "jobs"))))

(def cli
  {:doc "Job commands"
   :options 
   [["-e" "--environment <env>" "Environment"]
    ["-p" "--profile <profile>" "Service profile"]]
   :commands {"create" create
              "delete" delete
              "list" list}})
