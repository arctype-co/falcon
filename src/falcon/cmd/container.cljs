(ns falcon.cmd.container
  (:require
    [clojure.string :as string]
    [cljs.core.async :as async :refer [<!]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.core :as core :refer [map-keys] :refer-macros [require-arguments]]
    [falcon.config :as config-ns]
    [falcon.schema :as schema]
    [falcon.shell :as shell]
    [falcon.shell.docker :as docker]
    [falcon.shell.m4 :as m4]
    [falcon.shell.make :as make])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(defn- container-path
  [& path]
  (string/join "/" (concat ["cloud/container"] path)))

(defn- m4-defs
  [{:keys [config git-tag] :as opts} params]
  (merge (m4/defs opts)
         {"GIT_TAG" git-tag
          "LOGGLY_TOKEN" (get-in config [:loggly :token])}
         (map-keys name (:m4-params (config-ns/container opts (:container params))))))

(defn- full-container-tag
  [repository container tag]
  (str repository "/" container ":" tag))

(S/defn build
  "Build a docker image. Returns channel with tag."
  [{:keys [no-cache git-tag container-tag repository] :as opts} args]
  (require-arguments
    args
    (fn [container]
      (let [container-tag (or container-tag (core/new-tag))
            params {:container container
                    :container-tag container-tag}
            defs (m4-defs opts params)]
        (core/print-summary "Building container" opts params)
        (go
          (<! (-> (make/run defs ["-C" (container-path container)])))
          (<! (-> (m4/write defs
                            [(container-path container "Dockerfile.m4")]
                            (container-path container "Dockerfile"))
                  (shell/check-status)))
          (<! (-> (docker/build
                    {:no-cache no-cache}
                    [(str "-t=" (full-container-tag repository container container-tag))
                     (container-path container)])
                  (shell/check-status)))
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

(def cli 
  {:doc "Container management"
   :options [["-t" "--git-tag <tag>" "Git tag"
              :default "master"]
             ["-c" "--container-tag <tag>" "Container tag"]
             ["-n" "--no-cache" "Disable docker cache"
              :default false]]
   :commands {"build" build
              "push" push}})
