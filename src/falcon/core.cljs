(ns falcon.core
  (:require
    [cljs.core.async :as async]
    [cljs.pprint :refer [pprint]]
    [goog.string :as gstring]
    [goog.string.format :as gformat]))

(defn new-tag
  []
  (let [now (js/Date.)]
    (gstring/format "%04d-%02d-%02d.%02d%02d%02d"
                    (.getFullYear now) (+ 1 (.getMonth now)) (.getDate now)
                    (.getHours now) (.getMinutes now) (.getSeconds now))))

(defn safe-wait
  []
  (println "Waiting 10 seconds...")
  (async/timeout 10000))

(defn print-summary
  [message opts details]
  (println message)
  (pprint (merge (select-keys opts [:environment :cluster]) details)))
