(ns falcon.cmd.container
  (:require
    [clojure.string :as string]
    [cljs.core.async :as async :refer [<!]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.core :as core :refer [map-keys species-path] :refer-macros [require-arguments]]
    [falcon.config :as config-ns :refer [full-container-tag]]
    [falcon.schema :as schema]
    [falcon.shell :as shell]
    [falcon.shell.docker :as docker]
    [falcon.shell.m4 :as m4]
    [falcon.shell.make :as make])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(defn- push-container
  [opts container container-tag]
  (go 
    (let [container-id (full-container-tag opts container container-tag)]
      (<! (-> (docker/push
                {}
                [container-id])
              (shell/check-status)))
      (println "Pushed container:" container-id)
      container-tag)))

(defn- build-container
  "Shallowly build a container"
  [{:keys [pull no-cache container-tag output dockerpath] :as opts} container push?]
  (let [opts (merge (config-ns/container opts container) opts)
            container-tag (or container-tag (core/new-tag))
            container-id (full-container-tag opts container container-tag)
            params {:container container
                    :container-tag container-tag}
            defs (m4/defs opts params)]
        (core/print-summary "Building container" opts params)
        (go
          (when-not dockerpath 
            (<! (-> (m4/write defs
                              [(species-path container "Dockerfile.m4")]
                              (species-path container "Dockerfile"))
                    (shell/check-status))))
          (<! (-> (docker/build
                    (cond-> {:no-cache no-cache
                             :pull pull}
                      (some? dockerpath)
                      (assoc :working-dir dockerpath))
                    [(str "-t=" container-id)
                     (if (some? dockerpath)
                       "."
                       (species-path container))])
                  (shell/check-status)))
          (<! (-> (docker/tag
                    {}
                    [container-id
                     (full-container-tag opts container "latest")])))
          (println "Built container:" container-id)
          (when (some? output)
            (core/write-file output container-id))
          (when push?
            (<! (push-container opts container container-tag))
            (<! (push-container opts container "latest")))
          container-tag)))

(def ^:private docker-image-rexp (js/RegExp. "^FROM\\s([^/]+)/(.*)"))
(def ^:private docker-image-with-tag-rexp (js/RegExp. "^FROM\\s([^/]+)/([^:]+):(.*)"))

(defn- parse-dockerpath-from
  [dockerpath-path]
  (go
    (loop [lines (.split (core/read-file dockerpath-path) "\n")]
      (when-let [line (first lines)]
        (if-let [matches (re-matches docker-image-with-tag-rexp line)]
          (rest matches)
          (if-let [matches (re-matches docker-image-rexp line)]
            (rest matches)
            (recur (rest lines))))))))

(defn- build-deep-container
  [{:keys [container-tag] :as opts} container push?]
  (go
    ; write the dockerpath
    (let [dockerpath-path (species-path container "Dockerfile")
          params {:container container
                  :container-tag container-tag}
          defs (m4/defs opts params)]
      (<! (-> (m4/write defs
                        [(species-path container "Dockerfile.m4")]
                        dockerpath-path)
              (shell/check-status)))
      (if-let [[parent-repository parent-image parent-tag] (<! (parse-dockerpath-from dockerpath-path))]
        (do
          ; if we can build the parent, recursively build-deep
          ; else we are as far as we can go, build the container
          (when (contains?(set (core/all-clouds)) parent-repository)
            (<! (build-deep-container (dissoc opts :container-tag) parent-image push?)))
          (<! (build-container opts container push?)))
        (throw (ex-info (str "Failed to parse Dockerfile FROM declaration in " dockerpath-path)
                        {:container container
                         :path dockerpath-path}))))))

(S/defn build
  "Build a docker image. Returns channel with tag."
  [{:keys [deep no-cache container-tag] :as opts} args]
  (require-arguments
    args
    (fn [container]
      (if deep
        (build-deep-container opts container false)
        (build-container opts container false)))))

(S/defn push
  "Push a docker image to the repository. Returns channel with status."
  [{:keys [container-tag] :as opts} args]
  (require-arguments
    args
    (fn [container]
      (let [container-tag (or container-tag (throw (js/Error. "--container-tag required")))]
        (push-container opts container container-tag)))))

(S/defn publish
  "Build & push a docker image to the repository. Returns channel with status."
  [{:keys [deep] :as opts} args]
  (require-arguments
    args
    (fn [container]
      (if deep
        (build-deep-container opts container true)
        (build-container opts container true)))))

(def cli 
  {:doc "Container management"
   :options (core/cli-options
              [["-t" "--git-tag <tag>" "Git tag"]
               ["-c" "--container-tag <tag>" "Container tag"]
               ["-n" "--no-cache" "Disable docker cache" :default false]
               ["-p" "--[no-]pull" "Always pull latest image" :default true]
               ["-o" "--output <file>" "Output data file. Contains full name of built image."]
               ["-D" "--deep" "Build image dependencies recursively" :default false]
               ["-d" "--dockerpath <dir>" "Build Dockerfile in directory <dir>"]])
   :commands {"build" build
              "push" push
              "publish" publish}})
