.PHONY: all clean deps node_modules

all: node_modules bin/falcon.js

deps:
	lein deps

node_modules: deps project.clj
	lein npm update

bin/falcon.js:
	lein cljsbuild once

clean: 
	lein clean
