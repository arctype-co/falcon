(defproject sundbry/falcon "0.1.0-SNAPSHOT"
  :description "A Kubernetes configuration and deployment tool"

  :plugins
  [[lein-cljsbuild "1.1.7"]]

  :dependencies
  [[org.clojure/clojure "1.8.0"]
   [org.clojure/clojurescript "1.8.51"]
   [org.clojure/core.async "0.2.374"]
   [org.clojure/core.match "0.3.0-alpha4"]
   [org.clojure/tools.cli "0.3.7"]
   [prismatic/schema "1.0.1"]]

  :clean-targets [:target-path "index.js"]

  :cljsbuild
  {:builds
   {:client
    {:source-paths ["src"]
     :compiler
     {:output-to "index.js"
      :target :nodejs
      :optimizations :simple
      :pretty-print true
      :main "falcon.main"}}}})
