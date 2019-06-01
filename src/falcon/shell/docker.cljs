(ns falcon.shell.docker
  (:require
    [schema.core :as S]
    [falcon.schema :as schema]
    [falcon.shell :as core]))

(def Options
  {(S/optional-key :no-cache) S/Bool
   (S/optional-key :pull) S/Bool
   (S/optional-key :working-dir) S/Str})

(S/defn run :- schema/Chan
  [command :- S/Str
   {:keys [no-cache
           pull
           working-dir]} :- Options
   args :- [S/Str]]
  (let [flags (cond-> []
                no-cache (conj "--no-cache")
                pull (conj "--pull"))]
    (core/passthru (concat ["docker" command] flags args)
                   (cond-> {}
                     (some? working-dir) (assoc :cwd working-dir)))))

(def build (partial run "build"))
(def push (partial run "push"))
(def tag (partial run "tag"))
