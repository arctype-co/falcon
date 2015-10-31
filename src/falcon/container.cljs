(ns falcon.container
  (:require
    [schema.core :as S]
    [falcon.schema :as schema]
    [falcon.shell :as shell]))

(defn- container-dir
  []
  "cloud/container")

(defn- make-cmd
  [make-args]
  (shell/passthru (concat ["make" "-C" (container-dir)] make-args) {}))

(S/defn build
  "Build a docker container"
  [cfg :- schema/Config
   {:keys [arguments]} :- schema/Command]
  (let [image-name (second arguments)]
    (make-cmd [(str image-name "-image")])))

(S/defn push
  "Push a docker container"
  [cfg :- schema/Config
   {:keys [arguments]} :- schema/Command]
  (let [image-name (second arguments)]
    (make-cmd [(str image-name "-image-push")])))
