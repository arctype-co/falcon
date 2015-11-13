.PHONY: all auto clean deps node_modules

all: node_modules bin/falcon.js

deps:
	lein deps
	git submodule update --init

node_modules: deps project.clj
	lein npm update

bin/falcon.js:
	lein cljsbuild once

clean: 
	lein clean

auto:
	git pull
	git submodule update --init
	lein do clean, cljsbuild auto

update: 
	git pull
	git submodule update --init
	lein do clean, cljsbuild once
	
