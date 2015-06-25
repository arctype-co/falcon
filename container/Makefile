.PHONY: base-image jre-image lein-image app-image images push tag

REPO=gcr.io/etheride-984
TAG=latest # Override this with make TAG=foo
CLOUD=gcloud

all: images push

images: app-image-tag marketing-image-tag

app-image-tag: app-image
	docker tag -f etheride/app sundbry/etheride

marketing-image-tag: marketing-image
	docker tag -f etheride/marketing sundbry/etheride-marketing

geth-image-tag: geth-image
	docker tag -f etheride/geth $(REPO)/geth:$(TAG)

geth-image-push: geth-image-tag
	$(CLOUD) docker push $(REPO)/geth:$(TAG)

push: marketing-image-tag geth-image-push
	docker push sundbry/etheride
	docker push sundbry/etheride-marketing


base-image: base/Dockerfile
	docker build -t=etheride/base base

jre-image: base-image jre/Dockerfile
	docker build -t=etheride/jre jre

lein-image: jre-image lein/Dockerfile
	docker build -t=etheride/lein lein

marketing-image: lein-image marketing/Dockerfile marketing/marketing.tar.xz
	docker build -t=etheride/marketing marketing

app-image: lein-image app/Dockerfile app/app.tar.xz
	docker build -t=etheride/app app

geth-image: geth/Dockerfile
	docker build -t=etheride/geth geth
