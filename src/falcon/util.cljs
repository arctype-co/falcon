(ns falcon.util)

(defn rmerge
  "Recursive merge"
  [& maps]
  (if (map? (first maps))
    (apply merge-with rmerge maps)
    (first (remove nil? maps))))
