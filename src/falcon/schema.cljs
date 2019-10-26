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

(def BaseClusterConfig
  {(S/optional-key :kubeconfig) S/Str ; path to kubectl config
   (S/optional-key :kube-server) S/Str ; ip:port
   (S/optional-key :certificate-authority) S/Str
   (S/optional-key :client-certificate) S/Str
   (S/optional-key :client-key) S/Str})

(def VagrantClusterParams
  {(S/required-key :NODES) S/Int
   (S/required-key :CHANNEL) S/Str
   (S/required-key :MASTER_MEM) S/Int
   (S/required-key :MASTER_CPUS) S/Int
   (S/required-key :NODE_MEM) S/Int
   (S/required-key :NODE_CPUS) S/Int
   (S/required-key :USE_DOCKERCFG) S/Bool
   (S/required-key :USE_KUBE_UI) S/Bool})

(def VagrantClusterConfig
  (merge BaseClusterConfig
         {(S/required-key :provider) (S/enum "vagrant")
          (S/required-key :params) VagrantClusterParams}))

(def UbuntuClusterConfig
  (merge BaseClusterConfig
         {(S/required-key :provider) (S/enum "ubuntu")
          (S/required-key :params) {S/Keyword S/Str}}))

(def NativeClusterConfig
  (merge BaseClusterConfig
         {(S/required-key :provider) (S/enum "native")}))

(def ClusterConfig
  (S/conditional #(= (:provider %) "vagrant") VagrantClusterConfig
                 #(= (:provider %) "ubuntu") UbuntuClusterConfig
                 #(= (:provider %) "native") NativeClusterConfig))

(def ContainerConfig
  {(S/optional-key :m4-params) {S/Keyword S/Any}
   (S/optional-key :git-tag) S/Str
   (S/optional-key :repository) S/Str
   (S/optional-key :registry-id) S/Str})

(def BaseServiceConfig
  {(S/optional-key :m4-params) {S/Keyword S/Any}
   (S/optional-key :container-tag) S/Str ; Default container tag
   (S/optional-key :secret-files) [S/Str]})

(def ServiceConfig
  (merge BaseServiceConfig
         {(S/optional-key :profiles) {S/Keyword BaseServiceConfig}}))

(def EnvironmentConfig
  {(S/required-key :services) {S/Keyword ServiceConfig}})

(def ConfigOptions
  {(S/required-key :cluster) S/Str
   (S/required-key :repository) S/Str
   (S/optional-key :environment) (S/maybe S/Str)
   (S/optional-key :load-state) S/Str
   S/Keyword S/Any})

(def Config
  {(S/optional-key :options) ConfigOptions
   (S/required-key :clusters) {S/Keyword ClusterConfig}
   (S/optional-key :containers) {S/Keyword ContainerConfig}
   (S/optional-key :environments) {S/Keyword EnvironmentConfig}})

(def Chan js/Object)
