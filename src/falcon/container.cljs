(ns falcon.container
  (:require
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.schema :as schema]
    [falcon.shell :as shell]))

(def ^:private options
  [["-x" "--container <name>" "Container name"]])

(defn- container-dir
  []
  "cloud/container")

(defn- make-cmd
  [make-args]
  (shell/passthru (concat ["make" "-C" (container-dir)] make-args) {}))

(S/defn build
  "Build a docker image"
  [image :- S/Str args]
  (make-cmd [(str image "-image")]))

(S/defn push
  "Push a docker image to the repository"
  [image :- S/Str args]
  (make-cmd [(str image "-image-push")]))

(S/defn command
  "Return the cluster configuration"
  [function
   config :- schema/Config
   {:keys [arguments]} :- schema/Command]
  (let [{:keys [options errors summary]} (cli/parse-opts arguments options)
        {:keys [container]} options]
    (cond
      (some? errors) (println errors)
      (some? container) (function container arguments)
      :default (println summary))))
