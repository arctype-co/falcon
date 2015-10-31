(ns falcon.shell.docker
  (:require
    [schema.core :as S]
    [falcon.schema :as schema]
    [falcon.shell :as core]))

(def Options
  {(S/optional-key :no-cache) S/Bool})

(S/defn build :- schema/Chan
  [{:keys [no-cache]} :- Options
   args :- [S/Str]]
  (let [flags (cond-> []
                no-cache (conj "--no-cache=true"))]
    (core/passthru (concat ["docker" "build"] flags args))))
