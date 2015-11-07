# We want to keep regenerating Dockerfiles
.PHONY: app/Dockerfile crossbar/Dockerfile eth/Dockerfile geth/Dockerfile jre/Dockerfile lein/Dockerfile mysql/Dockerfile openvpn/Dockerfile pypy/Dockerfile riak-kv/Dockerfile www/Dockerfile

REPO=creeatist
TAG=latest
DOCKER=docker
DOCKERBUILD=docker build
#DOCKERBUILD=docker build --no-cache=true
M4=m4 -D__REPOSITORY__=$(REPO)
APP_TAG=master

all: 

clean:
	find . -name Dockerfile -maxdepth 2 -execdir rm -f {} \;

app/Dockerfile: app/Dockerfile.m4
	$(M4) -D__APP_TAG__=$(APP_TAG) $^ > $@

app-image: lein-image app/Dockerfile
	docker build -t=$(REPO)/app:$(TAG) app

app-image-push: app-image
	$(DOCKER) push $(REPO)/app:$(TAG)

base/Dockerfile: base/Dockerfile.m4
	$(M4) $^ > $@

base-image: base/Dockerfile
	$(DOCKERBUILD) -t=$(REPO)/base:$(TAG) base

base-image-push: base-image
	$(DOCKER) push $(REPO)/base:$(TAG)

chowder-api/Dockerfile: chowder-api/Dockerfile.m4
	$(M4) -D__APP_TAG__=$(APP_TAG) $^ > $@

crossbar/Dockerfile: crossbar/Dockerfile.m4
	$(M4) $^ > $@

crossbar-image: pypy-image crossbar/Dockerfile
	$(DOCKERBUILD) -t=$(REPO)/crossbar:$(TAG) crossbar

crossbar-image-push: crossbar-image
	$(DOCKER) push $(REPO)/crossbar:$(TAG)

eth/Dockerfile: eth/Dockerfile.m4
	$(M4) $^ > $@

eth-image: eth/Dockerfile
	$(DOCKERBUILD) -t=$(REPO)/eth:$(TAG) eth

eth-image-push: eth-image
	$(DOCKER) push $(REPO)/eth:$(TAG)

geth/Dockerfile: geth/Dockerfile.m4
	$(M4) $^ > $@

geth-image: geth/Dockerfile
	$(DOCKERBUILD) -t=$(REPO)/geth:$(TAG) geth

geth-image-push: geth-image
	$(DOCKER) push $(REPO)/geth:$(TAG)

jre/Dockerfile: jre/Dockerfile.m4
	$(M4) $^ > $@

jre-image: base-image jre/Dockerfile
	$(DOCKERBUILD) -t=$(REPO)/jre:$(TAG) jre

jre-image-push: jre-image
	$(DOCKER) push $(REPO)/jre:$(TAG)

lein/Dockerfile: lein/Dockerfile.m4
	$(M4) $^ > $@

lein-image: jre-image lein/Dockerfile
	$(DOCKERBUILD) -t=$(REPO)/lein:$(TAG) lein

lein-image-push: lein-image
	$(DOCKER) push $(REPO)/lein:$(TAG)

mysql/Dockerfile: mysql/Dockerfile.m4
	$(M4) $^ > $@

mysql-image: base-image mysql/Dockerfile
	$(DOCKERBUILD) -t $(REPO)/mysql:$(TAG) mysql

mysql-image-push: mysql-image
	$(DOCKER) push $(REPO)/mysql:$(TAG)

openvpn/openvpn.tar.gz: openvpn/openvpn
	# TODO this config is insecure (has private key files)
	tar -czf openvpn/openvpn.tar.gz -C openvpn/openvpn .

openvpn/Dockerfile: openvpn/Dockerfile.m4
	$(M4) $^ > $@

openvpn-image: openvpn/Dockerfile openvpn/openvpn.tar.gz
	$(DOCKERBUILD) -t $(REPO)/openvpn:$(TAG) openvpn

openvpn-image-push: openvpn-image
	$(DOCKER) push $(REPO)/openvpn:$(TAG)

pypy/Dockerfile: pypy/Dockerfile.m4
	$(M4) $^ > $@

pypy-image: base-image pypy/Dockerfile
	$(DOCKERBUILD) -t $(REPO)/pypy:$(TAG) pypy

pypy-image-push: pypy-image
	$(DOCKER) push $(REPO)/pypy:$(TAG)

riak-kv/Dockerfile: riak-kv/Dockerfile.m4
	$(M4) -D__RIAK_VERSION__=2.1.1-1 $^ > $@

riak-kv-image: base-image riak-kv/Dockerfile
	$(DOCKERBUILD) -t $(REPO)/riak-kv:$(TAG) riak-kv

riak-kv-image-push: riak-kv-image
	$(DOCKER) push $(REPO)/riak-kv:$(TAG)

www/Dockerfile: www/Dockerfile.m4
	$(M4) $^ > $@
