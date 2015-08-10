REPO=gcr.io/etheride-984
TAG=latest
ENV=dev # dev, prod
DOCKER_TAG=$(TAG)
M4=m4 -D__REPOSITORY__=$(REPO) -D__TAG__=$(TAG) -D__DOCKER_TAG__=$(DOCKER_TAG) -D__ENVIRONMENT__=$(ENV)
CONTROLLERS=app-controller.yml crossbar-controller.yml www-controller.yml

.PHONY: $(CONTROLLERS)

all: $(CONTROLLERS)

app-controller.yml: app-controller.yml.m4
	$(M4) $^ > $@

app-service.yml: app-service.yml.m4
	$(M4) $^ > $@

crossbar-controller.yml: crossbar-controller.yml.m4
	$(M4) $^ > $@

crossbar-service.yml: crossbar-service.yml.m4
	$(M4) $^ > $@

www-controller.yml: www-controller.yml.m4
	$(M4) $^ > $@
