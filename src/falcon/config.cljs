(ns falcon.config
  (:require
    [cljs.core.async :as async]
    [cljs.pprint :refer [pprint]]
    [schema.core :as S]
    [falcon.schema :as schema])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(def ^:private fs (js/require "fs"))
(def ^:private yamljs (js/require "yamljs"))

(S/defn read-yml :- schema/Config
  [config-path]
  (let [buf (.readFileSync fs config-path)
        cfg (js->clj (.parse yamljs (str buf)) :keywordize-keys true)]
    (println "Loaded config:")
    (pprint cfg)
    cfg))
