.PHONY: all auto clean deps node_modules

all: index.js

deps:
	lein deps
	git submodule update --init

node_modules:
	npm install

index.js: node_modules
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
