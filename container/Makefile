.PHONY: base-image jre-image lein-image app-image images push tag

REPO=gcr.io/etheride-984
TAG=latest # Override this with make TAG=foo
CLOUD=gcloud
DOCKERBUILD=docker build
#DOCKERBUILD=docker build --no-cache=true

all: images push

clean:
	rm -f app/etheride.tar.xz

images: app-image-tag marketing-image-tag

push: marketing-image-tag geth-image-push
	docker push sundbry/etheride
	docker push sundbry/etheride-marketing

app/etheride.tar.xz: ../../etheride
	make -C $^ clean
	tar -cJ -f $@ $^

app-image: lein-image app/Dockerfile app/etheride.tar.xz
	docker build -t=etheride/app app

app-image-tag: app-image
	docker tag -f etheride/app sundbry/etheride

base-image: base/Dockerfile
	$(DOCKERBUILD) -t=etheride/base base

crossbar-image: pypy-image crossbar/Dockerfile
	$(DOCKERBUILD) -t=etheride/crossbar crossbar

crossbar-image-tag: crossbar-image
	docker tag -f etheride/crossbar $(REPO)/crossbar:$(TAG)

crossbar-image-push: crossbar-image-tag
	$(CLOUD) docker push $(REPO)/crossbar:$(TAG)

eth-image: eth/Dockerfile
	$(DOCKERBUILD) -t=etheride/eth eth

eth-image-tag: eth-image
	docker tag -f etheride/eth $(REPO)/eth:$(TAG)

eth-image-push: eth-image-tag
	$(CLOUD) docker push $(REPO)/eth:$(TAG)

geth-image: geth/Dockerfile
	$(DOCKERBUILD) -t=etheride/geth geth

geth-image-tag: geth-image
	docker tag -f etheride/geth $(REPO)/geth:$(TAG)

geth-image-push: geth-image-tag
	$(CLOUD) docker push $(REPO)/geth:$(TAG)

jre-image: base-image jre/Dockerfile
	$(DOCKERBUILD) -t=etheride/jre jre

lein-image: jre-image lein/Dockerfile
	$(DOCKERBUILD) -t=etheride/lein lein

marketing-image: lein-image marketing/Dockerfile marketing/marketing.tar.xz
	$(DOCKERBUILD) -t=etheride/marketing marketing

marketing-image-tag: marketing-image
	docker tag -f etheride/marketing sundbry/etheride-marketing

openvpn/openvpn.tar.gz: openvpn/openvpn
	# TODO this config is insecure (has private key files)
	tar -czf openvpn/openvpn.tar.gz -C openvpn/openvpn .

openvpn-image: openvpn/Dockerfile openvpn/openvpn.tar.gz
	$(DOCKERBUILD) -t etheride/openvpn openvpn

openvpn-image-tag: openvpn-image
	docker tag -f etheride/openvpn $(REPO)/openvpn:$(TAG)

openvpn-image-push: openvpn-image-tag
	$(CLOUD) docker push $(REPO)/openvpn:$(TAG)

pypy-image: base-image pypy/Dockerfile
	$(DOCKERBUILD) -t etheride/pypy pypy

pypy-image-tag: pypy-image
	docker tag -f etheride/pypy $(REPO)/pypy:$(TAG)

pypy-image-push: pypy-image-tag
	$(CLOUD) docker push $(REPO)/pypy:$(TAG)
