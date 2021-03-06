(ns falcon.core
  (:refer-clojure :exclude [exists?])
  (:require
    [fs]
    [yamljs]
    [cljs.core.async :refer [<!]]
    [clojure.string :as string]
    [cljs.core.async :as async]
    [cljs.pprint :refer [pprint]]
    [goog.string :as gstring]
    [goog.string.format :as gformat]
    [falcon.config :as config-ns])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(def ^:private clouds-path "./.")

(def ^:private clouds-cache (atom nil)) ; List of clouds available

(defn error
  [message]
  (js/Error. message))

(defn all-clouds
  []
  "Return a list of clouds available"
  (swap! clouds-cache
         (fn [clouds-val]
           (if (some? clouds-val)
             clouds-val
             (let [contents (vec (.readdirSync fs clouds-path))]
               (->> contents
                    (filter 
                      (fn [dirent]
                        (let [stat (.statSync fs (str clouds-path "/" dirent))]
                          (.isDirectory stat))))))))))

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

(defn base64
  [buf]
  (.toString buf "base64"))

(defn map-keys
  [key-fn dict]
  (into {} (map (fn [[k v]] [(key-fn k) v])) dict))

(defn read-file
  [file-path]
  (.readFileSync fs file-path #js {:encoding "utf8" :flag "r"}))

(defn read-file-buffer
  [file-path]
  (.readFileSync fs file-path #js {:flag "r"}))

(defn write-file
  [file-path buf]
  (.writeFileSync fs file-path buf {:encoding "utf8" :flag "w"}))

(defn exists?
  [path]
  (try 
    (.statSync fs path)
    true
    (catch js/Error e false)))

(defn print-summary
  [message opts details]
  (println message)
  (let [data (merge (select-keys opts [:environment :cluster :profile]) details)]
    (pprint data)
    (when-let [state-file (:state-file opts)]
      (let [state-buf (.from js/Buffer (.stringify yamljs (clj->js data)))]
        (write-file state-file state-buf)))))

(def species-name config-ns/species-name)

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

(defn profiles
  [opts svc]
  (keys (:profiles (config-ns/service opts svc))))

(defn cli-options
  [specific-options]
  (vec (concat [["-s" "--state-file <file>" "Output generated state to a file for use in CD pipelines"]
                ["-l" "--load-state <file>" "Load parameters from a state file"]]
               specific-options)))
