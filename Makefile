.PHONY: all clean

node_modules: project.clj
	lein npm update

bin/falcon.js:
	lein cljsbuild once

all: node_modules bin/falcon.js

clean: 
	lein clean
