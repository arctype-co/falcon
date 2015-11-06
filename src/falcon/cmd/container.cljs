(ns falcon.cmd.container
  (:require
    [clojure.string :as string]
    [cljs.core.async :as async :refer [<!]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.core :as core :refer-macros [require-arguments]]
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
  [repository container tag]
  (str repository "/" container ":" tag))

(S/defn build :- schema/Chan
  "Build a docker image. Returns channel with tag."
  [{:keys [no-cache tag config]} args]
  (require-arguments
    (rest args)
    (fn [container]
      (let [tag (or tag (core/new-tag))]
        (go
          (<! (-> (make/run (make-opts tag)
                            ["-C" (make-path) (str container "/Dockerfile")])
                  (shell/check-status)))
          (<! (-> (docker/build
                    {:no-cache no-cache}
                    [(str "-t=" (container-tag (:repository config) container tag)) (make-path container)])
                  (shell/check-status)))
          (println "Container" container "built with tag:" tag)
          tag)))))

(S/defn push :- schema/Chan
  "Push a docker image to the repository. Returns channel with status."
  [{:keys [config tag]} args]
  (require-arguments
    (rest args) 
    (fn [container]
      (let [tag (or tag (core/new-tag))]
        (go 
          (<! (-> (docker/push
                    {}
                    [(container-tag (:repository config) container tag)])
                  (shell/check-status))))))))

(def cli 
  {:doc "Container management"
   :options [["-t" "--tag <tag>" "Container tag"
              :default nil] 
             ["-n" "--no-cache" "Disable docker cache"
              :default false]]
   :commands {"build" build
              "push" push}})
