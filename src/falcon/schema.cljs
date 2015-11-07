(ns falcon.schema
  (:require
    [clojure.string :as string]
    [schema.core :as S]))

(def Options
  {S/Keyword S/Any})

(def Command
  {:options Options
   :arguments [S/Str]
   (S/optional-key :errors) S/Any
   (S/optional-key :summary) S/Any})

(def VagrantBaseIp
  (S/pred (fn [addr] (= 4 (count (string/split addr #"\."))))))

(def ClusterConfig
  {(S/required-key :provider) (S/enum "vagrant")
   (S/required-key :nodes) S/Int
   (S/required-key :coreos-channel) S/Str
   (S/required-key :master-mem-mb) S/Int
   (S/required-key :master-cpus) S/Int
   (S/required-key :node-mem-mb) S/Int
   (S/required-key :node-cpus) S/Int
   (S/required-key :kube-ui) S/Bool
   (S/optional-key :dockercfg) S/Str 
   (S/optional-key :base-ip) VagrantBaseIp})

(def EnvironmentConfig
  {})

(def Config
  {(S/required-key :clusters) {S/Keyword ClusterConfig}
   (S/required-key :repository) S/Str})

(def Chan js/Object)
