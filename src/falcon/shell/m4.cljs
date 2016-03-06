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
  [{:keys [environment repository profile] :as opts} :- schema/Options
   {:keys [container-tag controller-tag service] :as params}]
  (merge
    {"REPOSITORY" repository 
     "ENVIRONMENT" environment
     "PROFILE" profile
     "SECRET" service ; for backwards compatibility
     "SERVICE" service
     "CONTAINER_TAG" container-tag
     "CONTROLLER_TAG" controller-tag}
    (when (some? service)
      (map-keys name (:m4-params (config-ns/service opts service))))))

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
