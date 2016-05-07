(defproject sundbry/falcon "0.1.0-SNAPSHOT"
  :description "A Kubernetes configuration and deployment tool"

  :plugins
  [[lein-cljsbuild "1.1.3"]
   [arohner/lein-npm "0.4.1"]]

  :dependencies
  [[org.clojure/clojure "1.8.0"]
   [org.clojure/clojurescript "1.8.51"]
   [org.clojure/core.async "0.2.374"]
   [org.clojure/core.match "0.3.0-alpha4"]
   [org.clojure/tools.cli "0.3.3"]
   [prismatic/schema "1.0.1"]]

  :node-dependencies [yamljs "0.2.4"]

  :clean-targets [:target-path "bin/falcon.js"]

  :cljsbuild
  {:builds
   {:client
    {:source-paths ["src"]
     :compiler
     {:output-to "bin/falcon.js"
      :target :nodejs
      :optimizations :simple
      :pretty-print true
      :main "falcon.main"}}}})
