(ns falcon.cmd.container
  (:require
    [clojure.string :as string]
    [cljs.core.async :as async :refer [<!]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.core :as core]
    [falcon.schema :as schema]
    [falcon.shell :as shell]
    [falcon.shell.docker :as docker]
    [falcon.shell.make :as make])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(defn- make-path
  [& path]
  (string/join "/" (concat ["cloud/container"] path)))

(defn- make-opts
  [tag]
  {"TAG" tag})

(defn- container-tag
  [container tag]
  (str repository "/" container ":" tag))

(S/defn build :- schema/Chan
  "Build a docker image. Returns channel with tag."
  [{:keys [container no-cache tag]} args]
  (let [tag (or tag (core/new-tag))]
    (go
      (<! (-> (make/run (make-opts tag)
                        ["-C" (make-path) (str container "/Dockerfile")])
              (shell/check-status)))
      (<! (-> (docker/build
                             {:no-cache no-cache}
                             [(str "-t=" (container-tag container tag)) (make-path container)])
                           (shell/check-status)))
      (println "Container" container "built with tag:" tag)
      tag)))

(S/defn push :- schema/Chan
  "Push a docker image to the repository. Returns channel with status."
  [{:keys [container tag]} args]
  (let [tag (or tag (core/new-tag))]
    (go 
      (<! (-> (docker/push
                {}
                [(container-tag container tag)])
              (shell/check-status))))))

(def cli 
  {:doc "Container management"
   :options [["-x" "--container <name>" "Container name"]
             ["-t" "--tag <tag>" "Container tag"
              :default nil]
             ["-r" "--repository <name>" "Docker repository"
              :default "creeatist"]
             ["-n" "--no-cache" "Disable docker cache"
              :default false]]
   :commands {"build" build
              "push" push}})
