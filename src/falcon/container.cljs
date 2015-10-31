(ns falcon.container
  (:require
    [clojure.string :as string]
    [cljs.core.async :as async :refer [<!]]
    [cljs.tools.cli :as cli]
    [goog.string :as gstring]
    [goog.string.format :as gformat]
    [schema.core :as S]
    [falcon.schema :as schema]
    [falcon.shell :as shell]
    [falcon.shell.docker :as docker]
    [falcon.shell.make :as make])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(def ^:private cli-options
  [["-x" "--container <name>" "Container name"]
   ["-t" "--tag <tag>" "Container tag"
    :default nil]
   ["-n" "--no-cache" "Disable docker cache"
    :default false]])

(def ^:private repository "creeatist")

(defn- make-path
  [& path]
  (string/join "/" (concat ["cloud/container"] path)))

(defn new-tag
  []
  (let [now (js/Date.)]
    (str (.getFullYear now) "-" (+ 1 (.getMonth now)) "-" (.getDate now)
         "." (gstring/format "%02d%02d%02d" (.getHours now) (.getMinutes now) (.getSeconds now)))))

(defn- make-opts
  [tag]
  {"TAG" tag})

(S/defn build
  "Build a docker image"
  [{:keys [container no-cache tag]} args]
  (let [tag (or tag (new-tag))]
    (go
      (<! (make/run (make-opts tag)
                    ["-C" (make-path) (str container "/Dockerfile")]))
      (<! (docker/build
            {:no-cache no-cache}
            [(str "-t=" repository "/" container ":" tag) (make-path container)])))))

(S/defn push
  "Push a docker image to the repository"
  [{:keys [container no-cache tag]} :- S/Str args]
  (let [tag (or tag (new-tag))]
    (go 
      (<! (make/run (make-opts tag)
                    ["-C" (make-path) (str container "-image-push")])))))

(S/defn command
  "Return the cluster configuration"
  [function
   config :- schema/Config
   {:keys [arguments]} :- schema/Command]
  (let [{:keys [options errors summary]} (cli/parse-opts arguments cli-options)
        {:keys [container no-cache]} options]
    (cond
      (some? errors) (println errors)
      (some? container) (function options arguments)
      :default (println summary))))
