(ns falcon.cmd)

#_(defmacro ns-command
  [cmd-ns]
  (let [{:keys [options] :as meta-data} (meta cmd-ns)
        cmd-ns-str (str cmd-ns)] 
    `(identity {:fn (-> )
                #_(loop [result# js/global
                       parts# (vec (.split ~cmd-ns-str "."))]
                  (println "parts")
                  (println parts#)
                  (if-let [part# (next parts#)] 
                    (do
                      (println part#)
                      (recur result# (rest parts#))
                      #_(recur (aget result# part#) (rest parts#)))
                    (aget result# "falcon")))})
   ; `(fn [cli-args#]
   ;    (~exec! ~commands ~options cli-args#))
))
