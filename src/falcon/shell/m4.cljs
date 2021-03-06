(ns falcon.shell.m4
  (:require
    [schema.core :as S]
    [falcon.schema :as schema]
    [falcon.core :as core :refer [map-keys]]
    [falcon.config :as config-ns]
    [falcon.shell :as shell]))

(def Defs
  {S/Str (S/maybe S/Any)})

(S/defn ^:private m4-defs :- Defs
  [{:keys [environment repository profile git-tag] :as opts} :- schema/Options
   {:keys [arguments container container-tag controller-tag service] :as params}]
  (let [image (or container service)
        service-name (config-ns/short-name service)]
    (merge
      {"ARGUMENTS" arguments
       "REPOSITORY" repository 
       "ENVIRONMENT" environment
       "GIT_TAG" git-tag
       "PROFILE" profile
       "SECRET" service-name ; for backwards compatibility
       "CONTAINER" container
       "SERVICE" service-name
       "CONTAINER_TAG" container-tag
       "CONTROLLER_TAG" controller-tag
       "TIMESTAMP" (str (.getTime (js/Date.)))}
      (when (some? container)
        (merge (map-keys name (:m4-params (config-ns/container opts container)))))
      (when (some? service)
        (map-keys name (:m4-params (config-ns/service opts service))))
      (when (some? image)
        {"IMAGE" (config-ns/full-container-tag opts image container-tag)}))))

(defn defs
  ([opts] (m4-defs opts nil))
  ([opts params] (m4-defs opts params)))

(S/defn write :- schema/Chan
  [local-defs :- Defs
   m4-args :- [S/Str]
   to-path :- S/Str]
  (let [top-include-dir (core/cloud-path)
        def-params (map (fn [[k v]] (str "-D" k "=" v)) local-defs)]
    (shell/write (concat ["m4" "-I" top-include-dir] def-params m4-args) {} to-path)))
