(ns falcon.schema
  (:require
    [clojure.string :as string]
    [schema.core :as S]))

(def Options
  {S/Keyword S/Any})

(def Command
  {(S/required-key :options) Options
   (S/required-key :arguments) [S/Str]
   (S/optional-key :errors) S/Any
   (S/optional-key :summary) S/Any})

(def VagrantBaseIp
  (S/pred (fn [addr] (= 4 (count (string/split addr #"\."))))))

(def VagrantClusterConfig
  {(S/required-key :provider) (S/enum "vagrant")
   (S/required-key :nodes) S/Int
   (S/required-key :coreos-channel) S/Str
   (S/required-key :master-mem-mb) S/Int
   (S/required-key :master-cpus) S/Int
   (S/required-key :node-mem-mb) S/Int
   (S/required-key :node-cpus) S/Int
   (S/required-key :kube-ui) S/Bool
   (S/optional-key :dockercfg) S/Str 
   ;(S/optional-key :base-ip) VagrantBaseIp ; buggy, not allowed
   })

(def UbuntuClusterConfig
  {(S/required-key :provider) (S/enum "ubuntu")
   (S/required-key :kube-server) S/Str ; ip:port
   (S/required-key :install-env) {S/Keyword S/Str}})

(def NativeClusterConfig
  {(S/required-key :provider) (S/enum "native")
   (S/required-key :kube-server) S/Str ; ip:port
   })

(def ClusterConfig
  (S/conditional #(= (:provider %) "vagrant") VagrantClusterConfig
                 #(= (:provider %) "ubuntu") UbuntuClusterConfig
                 #(= (:provider %) "native") NativeClusterConfig))

(def ContainerConfig
  {(S/optional-key :m4-params) {S/Keyword S/Any}})

(def ServiceConfig
  {(S/optional-key :m4-params) {S/Keyword S/Any}})

(def EnvironmentConfig
  {(S/required-key :services) {S/Keyword ServiceConfig}})

(def LogglyConfig
  {(S/required-key :token) S/Str})

(def ConfigOptions
  {(S/required-key :cluster) S/Str
   (S/required-key :repository) S/Str
   (S/required-key :environment) S/Str
   S/Keyword S/Any})

(def Config
  {(S/optional-key :options) ConfigOptions
   (S/required-key :clusters) {S/Keyword ClusterConfig}
   (S/optional-key :loggly) LogglyConfig
   (S/optional-key :containers) {S/Keyword ContainerConfig}
   (S/optional-key :environments) {S/Keyword EnvironmentConfig}})

(def Chan js/Object)
