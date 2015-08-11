REPO=gcr.io/etheride-984
TAG=latest
ENV=development # development, production
DOCKER_TAG=$(TAG)
M4=m4 -D__REPOSITORY__=$(REPO) -D__TAG__=$(TAG) -D__DOCKER_TAG__=$(DOCKER_TAG) -D__ENVIRONMENT__=$(ENV)
CONFIGS=app-controller.yml crossbar-controller.yml environment-namespace.yml www-controller.yml

.PHONY: $(CONFIGS)

all: 
	@echo Please specify a target: $(CONFIGS)

app-controller.yml: app-controller.yml.m4
	$(M4) $^ > $@

app-service.yml: app-service.yml.m4
	$(M4) $^ > $@

crossbar-controller.yml: crossbar-controller.yml.m4
	$(M4) $^ > $@

crossbar-service.yml: crossbar-service.yml.m4
	$(M4) $^ > $@

environment-namespace.yml: environment-namespace.yml.m4
	$(M4) $^ > $@

www-controller.yml: www-controller.yml.m4
	$(M4) $^ > $@
