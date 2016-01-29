(ns falcon.cmd.config
  (:refer-clojure :exclude [do])
  (:require
    [clojure.string :as string]
    [cljs.core.async :as async :refer [<!]]
    [cljs.pprint :refer [pprint]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.schema :as schema]
    [falcon.core :as core]
    [falcon.shell :as shell])
  (:require-macros
    [falcon.core :refer [require-arguments]]
    [cljs.core.async.macros :refer [go]]))

(S/defn pull
  "Pull latest configurations"
  [{:keys [git-branch]} args]
  (require-arguments
    args
    (fn [repository]
      (let [dir (core/cloud-path repository)]
        (println dir)
        (shell/passthru (concat ["git" "pull" "origin" git-branch])
                        {:cwd dir})))))

(S/defn push
  "Push configurations"
  [{:keys [git-branch]} args]
  (require-arguments
    args
    (fn [repository]
      (shell/passthru (concat ["git" "push" "origin" git-branch])
                      {:cwd (core/cloud-path repository)}))))

(def cli
  {:doc "Configuration management"
   :options
   [["-b" "--git-branch" "Git branch <branch>" "Git tag or branch" :default "master"]]
   :commands {"pull" pull
              "push" push}})
