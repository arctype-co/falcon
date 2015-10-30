(ns falcon.schema
  (:require
    [schema.core :as S]))

(def Command
  {:options {S/Str S/Str}
   :arguments [S/Str]
   (S/optional-key :errors) S/Any
   (S/optional-key :summary) S/Any})

(def ClusterConfig
  {(S/required-key "provider") (S/enum "vagrant")
   (S/required-key "nodes") S/Int
   (S/required-key "coreos-channel") S/Str
   (S/required-key "master-mem-mb") S/Int
   (S/required-key "master-cpus") S/Int
   (S/required-key "node-mem-mb") S/Int
   (S/required-key "node-cpus") S/Int
   (S/required-key "kube-ui") S/Bool})

(def EnvironmentConfig
  {(S/required-key "clusters") {S/Str ClusterConfig}})

(def Config
  {S/Str EnvironmentConfig})
