(ns falcon.core
  (:refer-clojure :exclude [exists?])
  (:require
    [cljs.core.async :refer [<!]]
    [clojure.string :as string]
    [cljs.core.async :as async]
    [cljs.pprint :refer [pprint]]
    [goog.string :as gstring]
    [goog.string.format :as gformat]
    [falcon.config :as config-ns])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(def ^:private fs (js/require "fs"))

(def ^:private morphs-path "./morph")

(def ^:private morphs-cache (atom nil)) ; List of morphs available

(defn- all-morphs
  []
  "Return a list of morphs available"
  (swap! morphs-cache
         (fn [morphs-val]
           (if (some? morphs-val)
             morphs-val
             (let [morphs-val (vec (.readdirSync fs morphs-path))]
               #_(println "Morphs loaded:" morphs-val)
               morphs-val)))))

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
  (pprint (merge (select-keys opts [:environment :cluster :profile]) details)))

(defn base64
  [str-val]
  (-> (js/Buffer. (str str-val)) (.toString "base64")))

(defn map-keys
  [key-fn dict]
  (into {} (map (fn [[k v]] [(key-fn k) v])) dict))

(defn read-file
  [file-path]
  (.readFileSync fs file-path #js {:encoding "utf8" :flag "r"}))

(defn exists?
  [path]
  (try 
    (.statSync fs path)
    true
    (catch js/Error e false)))

(defn species-path
  [species & inner-path]
  "Lookup a file within any morph"
  (loop [morphs (all-morphs)]
    (if-let [morph (first morphs)]
      (let [species-path (string/join "/" (concat [morphs-path morph species]))]
        (if (exists? species-path)
          (string/join "/" (concat [species-path] inner-path))
          (recur (rest morphs))))
      (throw (js/Error. (str "Morph species not found: " species))))))

(defn do-all-profiles
  "When the :all option is enabled, run do-fn for each profile option.
   do-fn must return a channel."
  [opts profile-keys do-fn]
  (if (:all opts)
    (go (doseq [profile-key profile-keys]
          (<! (do-fn (-> opts
                         (assoc :profile (name profile-key))
                         (dissoc :all))))))
    (do-fn opts)))
