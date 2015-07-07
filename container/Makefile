.PHONY: base-image jre-image lein-image app-image images push tag

REPO=gcr.io/etheride-984
TAG=latest # Override this with make TAG=foo
CLOUD=gcloud

all: images push

images: app-image-tag marketing-image-tag

push: marketing-image-tag geth-image-push
	docker push sundbry/etheride
	docker push sundbry/etheride-marketing

app-image: lein-image app/Dockerfile app/app.tar.xz
	docker build -t=etheride/app app

app-image-tag: app-image
	docker tag -f etheride/app sundbry/etheride

base-image: base/Dockerfile
	docker build -t=etheride/base base

eth-image: eth/Dockerfile
	docker build -t=etheride/eth eth

eth-image-tag: eth-image
	docker tag -f etheride/eth $(REPO)/eth:$(TAG)

eth-image-push: eth-image-tag
	$(CLOUD) docker push $(REPO)/eth:$(TAG)

geth-image: geth/Dockerfile
	docker build -t=etheride/geth geth

geth-image-tag: geth-image
	docker tag -f etheride/geth $(REPO)/geth:$(TAG)

geth-image-push: geth-image-tag
	$(CLOUD) docker push $(REPO)/geth:$(TAG)

jre-image: base-image jre/Dockerfile
	docker build -t=etheride/jre jre

lein-image: jre-image lein/Dockerfile
	docker build -t=etheride/lein lein

marketing-image: lein-image marketing/Dockerfile marketing/marketing.tar.xz
	docker build -t=etheride/marketing marketing

marketing-image-tag: marketing-image
	docker tag -f etheride/marketing sundbry/etheride-marketing

openvpn/openvpn.tar.gz: openvpn/openvpn
	# TODO this config is insecure (has private key files)
	tar -czf openvpn/openvpn.tar.gz -C openvpn/openvpn .

openvpn-image: openvpn/Dockerfile openvpn/openvpn.tar.gz
	docker build -t etheride/openvpn openvpn

openvpn-image-tag: openvpn-image
	docker tag -f etheride/openvpn $(REPO)/openvpn:$(TAG)

openvpn-image-push: openvpn-image-tag
	$(CLOUD) docker push $(REPO)/openvpn:$(TAG)
