.PHONY: base-image jre-image lein-image app-image images push tag

REPO=gcr.io/etheride-984
TAG=latest # Override this with make TAG=foo
GCLOUD=gcloud
DOCKERBUILD=docker build
#DOCKERBUILD=docker build --no-cache=true

all: images push

clean:
	rm -f app/etheride.tar.xz

images: app-image marketing-image

push: app-image-push marketing-image-push geth-image-push

app/etheride.tar.xz: ../../etheride
	make -C $^ clean
	tar -cJ -f $@ $^

app-image: lein-image app/Dockerfile app/etheride.tar.xz
	docker build -t=$(REPO)/app:$(TAG) app

app-image-push: app-image
	$(GCLOUD) docker push $(REPO)/app:$(TAG)

base-image: base/Dockerfile
	$(DOCKERBUILD) -t=$(REPO)/base:$(TAG) base

base-image-push: base-image
	$(GCLOUD) docker push $(REPO)/base:$(TAG)

crossbar-image: pypy-image crossbar/Dockerfile
	$(DOCKERBUILD) -t=$(REPO)/crossbar:$(TAG) crossbar

crossbar-image-push: crossbar-image
	$(GCLOUD) docker push $(REPO)/crossbar:$(TAG)

eth-image: eth/Dockerfile
	$(DOCKERBUILD) -t=$(REPO)/eth:$(TAG) eth

eth-image-push: eth-image
	$(GCLOUD) docker push $(REPO)/eth:$(TAG)

geth-image: geth/Dockerfile
	$(DOCKERBUILD) -t=$(REPO)/geth:$(TAG) geth

geth-image-push: geth-image
	$(GCLOUD) docker push $(REPO)/geth:$(TAG)

jre-image: base-image jre/Dockerfile
	$(DOCKERBUILD) -t=$(REPO)/jre:$(TAG) jre

jre-image-push: jre-image
	$(GCLOUD) docker push $(REPO)/jre:$(TAG)

lein-image: jre-image lein/Dockerfile
	$(DOCKERBUILD) -t=$(REPO)/lein:$(TAG) lein

lein-image-push: lein-image
	$(GCLOUD) docker push $(REPO)/lein:$(TAG)

marketing-image: lein-image marketing/Dockerfile marketing/marketing.tar.xz
	$(DOCKERBUILD) -t=$(REPO)/marketing:$(TAG) marketing

openvpn/openvpn.tar.gz: openvpn/openvpn
	# TODO this config is insecure (has private key files)
	tar -czf openvpn/openvpn.tar.gz -C openvpn/openvpn .

openvpn-image: openvpn/Dockerfile openvpn/openvpn.tar.gz
	$(DOCKERBUILD) -t $(REPO)/openvpn:$(TAG) openvpn

openvpn-image-push: openvpn-image
	$(GCLOUD) docker push $(REPO)/openvpn:$(TAG)

pypy-image: base-image pypy/Dockerfile
	$(DOCKERBUILD) -t $(REPO)/pypy:$(TAG) pypy

pypy-image-push: pypy-image
	$(GCLOUD) docker push $(REPO)/pypy:$(TAG)
