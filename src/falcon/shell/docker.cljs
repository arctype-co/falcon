(ns falcon.shell.docker
  (:refer-clojure :exclude [force])
  (:require
    [schema.core :as S]
    [falcon.schema :as schema]
    [falcon.shell :as core]))

(def Options
  {(S/optional-key :force) S/Bool
   (S/optional-key :no-cache) S/Bool})

(S/defn run :- schema/Chan
  [command :- S/Str
   {:keys [force no-cache]} :- Options
   args :- [S/Str]]
  (let [flags (cond-> []
                force (conj "-f")
                no-cache (conj "--no-cache=true"))]
    (core/passthru (concat ["docker" command] flags args))))

(def build (partial run "build"))
(def push (partial run "push"))
(def tag (partial run "tag"))
