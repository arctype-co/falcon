(ns falcon.schema
  (:require
    [schema.core :as S]))

(def Command
  {:options {S/Str S/Str}
   :arguments [S/Str]
   (S/optional-key :errors) S/Any
   (S/optional-key :summary) S/Any})
