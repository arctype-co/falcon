(ns falcon.shell
  (:require
    [clojure.string :as string]
    [cljs.core.async :as async] 
    [schema.core :as S]
    [falcon.schema :as schema])
  (:require-macros
    [cljs.core.async.macros :refer [go go-loop]]))

(def ^:private child-process (js/require "child_process"))
(def ^:private fs (js/require "fs"))
(def ^:private BIN-SH "/bin/bash")

(def SpawnParams
  {(S/optional-key :cwd) S/Str
   (S/optional-key :env) {S/Str S/Str}})

(def ProcessOutput
  {:stdout js/Object ; channel
   :stderr js/Object
   :return js/Object})

(defn- merge-env
  [override]
  (let [clone-env (->> (.-env js/process)
                       (.stringify js/JSON)
                       (.parse js/JSON))]
    (doseq [[k v] override]
      (aset clone-env k v))
    clone-env))

(defn check-status
  [chan]
  (go
    (let [result (async/<! chan)]
      (if (= 0 result)
        result
        (throw (js/Error. (str "Command failed with status:" result)))))))

(S/defn spawn :- ProcessOutput
  "Launch a process. Return output channels."
  [cmd :- [S/Str]
   {:keys [cwd env] :as params} :- SpawnParams]
  (let [out (async/chan 10)
        err (async/chan 10)
        ret (async/chan 1)
        penv (merge-env env)
        options (doto (new js/Object.)
                  (aset "cwd" cwd)
                  (aset "env" penv))
        proc (.spawn child-process (first cmd) (to-array (rest cmd)) options)]
    (-> proc .-stdout (.on "data"
                           (fn [buf]
                             (async/put! out buf))))
    (-> proc .-stderr (.on "data"
                           (fn [buf]
                             (async/put! err buf))))
    (-> proc (.on "close"
                  (fn [code]
                    (async/close! out)
                    (async/close! err)
                    (async/put! ret code)
                    (async/close! ret))))
    {:stdout out
     :stderr err
     :return ret}))

(defn- print-cmd
  [cmd {:keys [cwd]}]
  (if (some? cwd)
    (println cwd "$" (string/join " " cmd)) 
    (println "$" (string/join " " cmd))))

(S/defn passthru :- schema/Chan
  "Launch a process. Pass stdio through parent process. Return channel with return code."
  ([cmd :- [S/Str]] (passthru cmd {}))
  ([cmd :- [S/Str]
    {:keys [cwd env] :as params} :- SpawnParams]
   (let [cmd (vec cmd)
         _ (print-cmd cmd params)
         ret (async/chan 1)
         penv (merge-env env)
         options (doto (new js/Object.)
                   (aset "cwd" cwd)
                   (aset "env" penv)
                   (aset "stdio" "inherit"))
         proc (.spawn child-process (first cmd) (to-array (rest cmd)) options)]
     (-> proc (.on "close"
                   (fn [code]
                     (async/put! ret code)
                     (async/close! ret))))
     ret)))

(S/defn write :- schema/Chan
  "Launch a process. Pass stdin through parent process. Write stdout to file. Return channel with return code."
  [cmd :- [S/Str]
   {:keys [cwd env] :as params} :- SpawnParams
   out-file-path :- S/Str]
   (let [out-file (.openSync fs out-file-path "w")
         stdio #js [0 out-file 2]
         cmd (vec cmd)
         _ (println cmd ">" out-file-path)
         ret (async/chan 1)
         penv (merge-env env)
         options (doto (new js/Object.)
                   (aset "cwd" cwd)
                   (aset "env" penv)
                   (aset "stdio" stdio))
         proc (.spawn child-process (first cmd) (to-array (rest cmd)) options)]
     (-> proc (.on "close"
                   (fn [code]
                     (async/put! ret code)
                     (async/close! ret))))
     ret))
