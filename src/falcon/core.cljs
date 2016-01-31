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

(def ^:private clouds-path "./cloud")

(def ^:private clouds-cache (atom nil)) ; List of clouds available

(defn- all-clouds
  []
  "Return a list of clouds available"
  (swap! clouds-cache
         (fn [clouds-val]
           (if (some? clouds-val)
             clouds-val
             (let [clouds-val (vec (.readdirSync fs clouds-path))]
               #_(println "Morphs loaded:" clouds-val)
               clouds-val)))))

(defn new-tag
  []
  (let [now (js/Date.)]
    (gstring/format "%04d-%02d-%02d-%02d%02d%02d"
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

(defn species-name
  "Return the unqualified name of a cloud/species if it is qualified."
  [species]
  (if (<= 0 (.indexOf species "/"))
    ; qualified name
    (second (string/split species "/"))
    ; unqualified name
    species))

(defn cloud-path
  [& inner-path]
  (string/join "/" (concat [clouds-path] inner-path)))

(defn species-path
  [qualified-species & inner-path]
  "Lookup a file within any cloud"
  (let [[clouds species] (if (<= 0 (.indexOf qualified-species "/"))
                           ; qualified name
                           (let [[cloud species] (string/split qualified-species "/")]
                             [[cloud] species])
                           ; unqualified name
                           [(all-clouds) qualified-species])]
    (loop [clouds clouds]
    (if-let [cloud (first clouds)]
      (let [species-path (string/join "/" (concat [clouds-path cloud species]))]
        (if (exists? species-path)
          (string/join "/" (concat [species-path] inner-path))
          (recur (rest clouds))))
      (throw (js/Error. (str "Species not found: " qualified-species)))))))

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
