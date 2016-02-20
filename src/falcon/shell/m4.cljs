(ns falcon.shell.m4
  (:require
    [schema.core :as S]
    [falcon.schema :as schema]
    [falcon.core :as core]
    [falcon.shell :as shell]))

(def Defs
  {S/Str (S/maybe S/Any)})

(S/defn defs :- Defs
  [{:keys [environment repository profile service] :as opts} :- schema/Options]
  {"REPOSITORY" repository 
   "ENVIRONMENT" environment
   "PROFILE" profile})

(S/defn write :- schema/Chan
  [local-defs :- Defs
   m4-args :- [S/Str]
   to-path :- S/Str]
  (let [top-include-dir (core/cloud-path)
        def-params (map (fn [[k v]] (str "-D" k "=" v)) local-defs)]
    (shell/write (concat ["m4" "-I" top-include-dir] def-params m4-args) {} to-path)))
