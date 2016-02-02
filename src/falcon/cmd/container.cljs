(ns falcon.cmd.container
  (:require
    [clojure.string :as string]
    [cljs.core.async :as async :refer [<!]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.core :as core :refer [map-keys species-path] :refer-macros [require-arguments]]
    [falcon.config :as config-ns]
    [falcon.schema :as schema]
    [falcon.shell :as shell]
    [falcon.shell.docker :as docker]
    [falcon.shell.m4 :as m4]
    [falcon.shell.make :as make])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(defn- m4-defs
  [{:keys [config git-tag m4-params] :as opts} {:keys [container]}]
  (merge (m4/defs opts)
         {"GIT_TAG" git-tag
          "CONTAINER" container}
         (map-keys name m4-params)))

(defn- full-container-tag
  [repository container tag]
  (str repository "/" (core/species-name container) ":" tag))

(S/defn build
  "Build a docker image. Returns channel with tag."
  [{:keys [no-cache container-tag repository] :as opts} args]
  (require-arguments
    args
    (fn [container]
      (let [opts (merge (config-ns/container opts container) opts)
            container-tag (or container-tag (core/new-tag))
            container-id (full-container-tag repository container container-tag)
            params {:container container
                    :container-tag container-tag}
            defs (m4-defs opts params)]
        (core/print-summary "Building container" opts params)
        (go
          ; Run a Makefile if there is one
          (<! (-> (make/run defs ["-C" (species-path container) "container"])))
          (<! (-> (m4/write defs
                            [(species-path container "Dockerfile.m4")]
                            (species-path container "Dockerfile"))
                  (shell/check-status)))
          (<! (-> (docker/build
                    {:no-cache no-cache}
                    [(str "-t=" container-id)
                     (species-path container)])
                  (shell/check-status)))
          (<! (-> (docker/tag
                    {:force true}
                    [container-id
                     (full-container-tag repository container "latest")])))
          (println "Built container:" container-id)
          container-tag)))))

(S/defn push
  "Push a docker image to the repository. Returns channel with status."
  [{:keys [container-tag repository]} args]
  (require-arguments
    args
    (fn [container]
      (let [container-tag (or container-tag (throw (js/Error. "--container-tag required")))]
        (go 
          (<! (-> (docker/push
                    {}
                    [(full-container-tag repository container container-tag)])
                  (shell/check-status))))))))

(S/defn publish
  "Build & push a docker image to the repository. Returns channel with status."
  [{:keys [git-tag] :as opts} args]
  (require-arguments
    args
    (fn [container]
      (let [git-tag (or git-tag (throw (js/Error. "--git-tag required")))]
        (go 
          (let [container-tag (<! (build opts [container]))
                opts (assoc opts :container-tag container-tag)]
            (<! (push opts [container]))))))))

(def cli 
  {:doc "Container management"
   :options [["-t" "--git-tag <tag>" "Git tag"]
             ["-c" "--container-tag <tag>" "Container tag"]
             ["-n" "--no-cache" "Disable docker cache"
              :default false]]
   :commands {"build" build
              "push" push
              "publish" publish}})
