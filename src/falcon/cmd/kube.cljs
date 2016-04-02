(ns falcon.cmd.kube
  (:refer-clojure :exclude [do])
  (:require
    [clojure.string :as string]
    [cljs.core.async :as async :refer [<!]]
    [cljs.pprint :refer [pprint]]
    [cljs.tools.cli :as cli]
    [schema.core :as S]
    [falcon.schema :as schema]
    [falcon.shell :as shell]
    [falcon.shell.kubectl :as kubectl])
  (:require-macros
    [falcon.core :refer [require-arguments]]
    [cljs.core.async.macros :refer [go]]))

(S/defn do
  "Do a kubernetes command"
  [opts args]
  (go (<! (apply kubectl/run opts args))))

(S/defn version
  "Get kubernetes version"
  [options :- schema/Options args]
  (go (<! (kubectl/run options "version"))))

(def cli
  {:doc "Integrated kubectl commands"
   :options 
   [["-e" "--environment <env>" "Environment"]
    ["-f" "--follow" "Follow log tail"]]
   :commands {"do" do
              "version" version}})
