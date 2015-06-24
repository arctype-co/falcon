.PHONY: base-image jre-image lein-image app-image images push tag

all: images push

images: app-image-tag marketing-image-tag

app-image-tag: app-image
	docker tag -f etheride/app sundbry/etheride

marketing-image-tag: marketing-image
	docker tag -f etheride/marketing sundbry/etheride-marketing

geth-image-tag: geth-image
	docker tag -f etheride/geth sundbry/etheride-geth

push: marketing-image-tag
	docker push sundbry/etheride
	docker push sundbry/etheride-marketing
	docker push sundbry/etheride-geth

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
