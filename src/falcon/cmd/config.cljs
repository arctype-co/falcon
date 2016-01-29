(ns falcon.cmd.config
  (:refer-clojure :exclude [do name remove])
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

(S/defn add
  "Add new config repository"
  [{:keys [git-branch]} args]
  (require-arguments
    args
    (fn [git-url name]
      (shell/passthru (concat ["git" "clone" git-url name])
                      {:cwd (core/cloud-path)}))))

(S/defn remove
  "Remove a config repository"
  [{:keys [git-branch]} args]
  (require-arguments
    args
    (fn [repository]
      (let [dir (core/cloud-path repository)]
        (go 
          (println "Warning: this will remove" dir)
          (<! (core/safe-wait))
          (<! (shell/passthru (concat ["rm" "-rf" dir]))))))))

(S/defn pull
  "Pull latest configurations"
  [{:keys [git-branch]} args]
  (require-arguments
    args
    (fn [repository]
      (shell/passthru (concat ["git" "pull" "origin" git-branch])
                      {:cwd (core/cloud-path repository)}))))

(S/defn push
  "Push configurations"
  [{:keys [git-branch]} args]
  (require-arguments
    args
    (fn [repository]
      (shell/passthru (concat ["git" "push" "origin" git-branch])
                      {:cwd (core/cloud-path repository)}))))

(S/defn status
  "Status of configuration"
  [opts args]
  (require-arguments
    args
    (fn [repository]
      (shell/passthru (concat ["git" "status"])
                      {:cwd (core/cloud-path repository)}))))

(def cli
  {:doc "Configuration management"
   :options
   [["-b" "--git-branch" "Git branch <branch>" "Git tag or branch" :default "master"]]
   :commands {"add" add
              "remove" remove
              "pull" pull
              "push" push
              "status" status}})
