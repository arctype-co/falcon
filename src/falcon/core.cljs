(ns falcon.core
  (:require
    [clojure.string :as string]
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

(defn base64
  [str-val]
  (-> (js/Buffer. (str str-val)) (.toString "base64")))

(defn map-keys
  [key-fn dict]
  (into {} (map (fn [[k v]] [(key-fn k) v])) dict))

(defn cloud-path
  [& path]
  (string/join "/" (concat ["cloud/service"] path)))
