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

openvpn/openvpn.tar.gz: openvpn/openvpn
	# TODO this config is insecure (has private key files)
	tar -czf openvpn/openvpn.tar.gz -C openvpn/openvpn .

openvpn/Dockerfile: openvpn/Dockerfile.m4
	$(M4) $^ > $@

openvpn-image: openvpn/Dockerfile openvpn/openvpn.tar.gz
	$(DOCKERBUILD) -t $(REPO)/openvpn:$(TAG) openvpn

openvpn-image-push: openvpn-image
	$(DOCKER) push $(REPO)/openvpn:$(TAG)
