(ns falcon.template
  (:require
    [clojure.string :as string]
    [cljs.core.async :as async :refer [<!]]
    [schema.core :as S]
    [falcon.core :refer [species-path]]
    [falcon.shell :as shell]
    [falcon.shell.m4 :as m4]))

(defn make-yml
  [yml-name opts {:keys [service] :as defs}]
  (let []
    (-> (m4/write (m4/defs opts defs)
                  [(species-path service (str yml-name ".m4"))]
                  (species-path service yml-name))
        (shell/check-status))))

(defn print-args
  "Return a template string from a sequence"
  [arg-list]
  (string/join ", " (map pr-str arg-list)))
