(ns falcon.core
  (:require
    [cljs.tools.cli :as cli]
    [cljs.nodejs :as nodejs]
    [schema.core :as S]))

(nodejs/enable-util-print!)

(def cli-options
  [["-e" "--environment ENV" "Environment"
    :default "development"]])

(defn -main [& args]
  (println "Foo!"))

(set! cljs.core/*main-cli-fn* -main)
