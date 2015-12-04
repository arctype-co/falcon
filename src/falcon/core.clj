(ns falcon.core)

(defmacro require-arguments
  [args fn-def]
  (let [fn-args (second fn-def)
        fn-body (drop 2 fn-def)
        fn-arg-names (vec (map str fn-args))
        bindings (->> (for [i (range (count fn-args))]
                        [(nth fn-args i) `(nth ~args ~i)])
                      (reduce concat)
                      (vec))]
    `(if (= (count ~fn-arg-names) (count ~args)) 
       (let ~bindings (do ~@fn-body))
       (println "Expected arguments:" ~fn-arg-names))))
