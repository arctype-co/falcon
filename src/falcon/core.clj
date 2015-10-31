(ns falcon.core)

(defmacro require-arguments
  [args fn-def]
  (let [fn-args (second fn-def)
        fn-body (drop 2 fn-def)
        bindings (->> (for [i (range (count fn-args))]
                        [(nth fn-args i) `(nth args i)])
                      (reduce concat)
                      (vec))
        bindings [(first fn-args) `(first ~args)
                  (second fn-args) `(second ~args)]]
    `(let ~bindings ~@fn-body)))
