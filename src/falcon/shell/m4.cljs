(ns falcon.shell.m4
  (:require
    [schema.core :as S]
    [falcon.schema :as schema]
    [falcon.shell :as shell]))

(def Defs
  {S/Str S/Str})

(S/defn base-defs :- Defs
  [{:keys [config environment] :as opts} :- schema/Options]
  {"__REPOSITORY__" (:repository config)
   "__ENVIRONMENT__" environment})

(S/defn write :- schema/Chan
  [defs :- Defs
   m4-args :- [S/Str]
   to-path :- S/Str]
  (let [def-params (map (fn [[k v]] (str "-D" k "=" v)) defs)]
    (shell/write (concat ["m4"] def-params m4-args) {} to-path)))
