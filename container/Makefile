# We want to keep regenerating Dockerfiles
.PHONY: app/Dockerfile crossbar/Dockerfile eth/Dockerfile geth/Dockerfile jre/Dockerfile lein/Dockerfile openvpn/Dockerfile pypy/Dockerfile riak-kv/Dockerfile www/Dockerfile

REPO=gcr.io/etheride-984
TAG=latest # Override this with make TAG=foo
GCLOUD=gcloud
DOCKERBUILD=docker build
#DOCKERBUILD=docker build --no-cache=true
M4=m4 -D__REPOSITORY__=$(REPO)
APP_TAG=master
WWW_TAG=master

all: 

clean:
	find . -name Dockerfile -maxdepth 2 -execdir rm -f {} \;

app/Dockerfile: app/Dockerfile.m4
	$(M4) -D__APP_TAG__=$(APP_TAG) $^ > $@

app-image: lein-image app/Dockerfile
	docker build -t=$(REPO)/app:$(TAG) app

app-image-push: app-image
	$(GCLOUD) docker push $(REPO)/app:$(TAG)

base/Dockerfile: base/Dockerfile.m4
	$(M4) $^ > $@

base-image: base/Dockerfile
	$(DOCKERBUILD) -t=$(REPO)/base:$(TAG) base

base-image-push: base-image
	$(GCLOUD) docker push $(REPO)/base:$(TAG)

crossbar/Dockerfile: crossbar/Dockerfile.m4
	$(M4) $^ > $@

crossbar-image: pypy-image crossbar/Dockerfile
	$(DOCKERBUILD) -t=$(REPO)/crossbar:$(TAG) crossbar

crossbar-image-push: crossbar-image
	$(GCLOUD) docker push $(REPO)/crossbar:$(TAG)

eth/Dockerfile: eth/Dockerfile.m4
	$(M4) $^ > $@

eth-image: eth/Dockerfile
	$(DOCKERBUILD) -t=$(REPO)/eth:$(TAG) eth

eth-image-push: eth-image
	$(GCLOUD) docker push $(REPO)/eth:$(TAG)

geth/Dockerfile: geth/Dockerfile.m4
	$(M4) $^ > $@

geth-image: geth/Dockerfile
	$(DOCKERBUILD) -t=$(REPO)/geth:$(TAG) geth

geth-image-push: geth-image
	$(GCLOUD) docker push $(REPO)/geth:$(TAG)

jre/Dockerfile: jre/Dockerfile.m4
	$(M4) $^ > $@

jre-image: base-image jre/Dockerfile
	$(DOCKERBUILD) -t=$(REPO)/jre:$(TAG) jre

jre-image-push: jre-image
	$(GCLOUD) docker push $(REPO)/jre:$(TAG)

lein/Dockerfile: lein/Dockerfile.m4
	$(M4) $^ > $@

lein-image: jre-image lein/Dockerfile
	$(DOCKERBUILD) -t=$(REPO)/lein:$(TAG) lein

lein-image-push: lein-image
	$(GCLOUD) docker push $(REPO)/lein:$(TAG)

openvpn/openvpn.tar.gz: openvpn/openvpn
	# TODO this config is insecure (has private key files)
	tar -czf openvpn/openvpn.tar.gz -C openvpn/openvpn .

openvpn/Dockerfile: openvpn/Dockerfile.m4
	$(M4) $^ > $@

openvpn-image: openvpn/Dockerfile openvpn/openvpn.tar.gz
	$(DOCKERBUILD) -t $(REPO)/openvpn:$(TAG) openvpn

openvpn-image-push: openvpn-image
	$(GCLOUD) docker push $(REPO)/openvpn:$(TAG)

pypy/Dockerfile: pypy/Dockerfile.m4
	$(M4) $^ > $@

pypy-image: base-image pypy/Dockerfile
	$(DOCKERBUILD) -t $(REPO)/pypy:$(TAG) pypy

pypy-image-push: pypy-image
	$(GCLOUD) docker push $(REPO)/pypy:$(TAG)

riak-kv/Dockerfile: riak-kv/Dockerfile.m4
	$(M4) -D__RIAK_VERSION__=2.1.1-1 $^ > $@

riak-kv-image: base-image riak-kv/Dockerfile
	$(DOCKERBUILD) -t $(REPO)/riak-kv:$(TAG) riak-kv

riak-kv-image-push: riak-kv-image
	$(GCLOUD) docker push $(REPO)/riak-kv:$(TAG)

www/Dockerfile: www/Dockerfile.m4
	$(M4) -D__WWW_TAG__=$(WWW_TAG) $^ > $@

www-image: lein-image www/Dockerfile
	$(DOCKERBUILD) -t=$(REPO)/www:$(TAG) www

www-image-push: www-image
	$(GCLOUD) docker push $(REPO)/www:$(TAG)
