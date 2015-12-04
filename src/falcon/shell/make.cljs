(ns falcon.shell.make
  (:require
    [schema.core :as S]
    [falcon.schema :as schema]
    [falcon.shell :as core]))

(def Options
  {S/Str (S/maybe S/Str)})

(S/defn run :- schema/Chan
  [make-opts :- Options
   make-args :- [S/Str]]
  (let [make-params (map (fn [[k v]] (str k "=" v)) make-opts)]
    (core/passthru (concat ["make"] make-args make-params))))
