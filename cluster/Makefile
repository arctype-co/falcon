REPO=gcr.io/etheride-984
TAG=latest
# development, production
ENV=development
DOCKER_TAG=$(TAG)
M4=m4 -D__REPOSITORY__=$(REPO) -D__TAG__=$(TAG) -D__DOCKER_TAG__=$(DOCKER_TAG) -D__ENVIRONMENT__=$(ENV)
KUBECTL=kubectl --namespace=$(ENV)
CONFIGS=app/controller.yml app/secrets.yml app/service.yml crossbar/controller.yml crossbar/service.yml environment-namespace.yml www-controller.yml
DEPLOYS=app/create app/delete app/update crossbar/create crossbar/delete

.PHONY: $(CONFIGS)

all: 
	@echo Please specify a configuration: $(CONFIGS)
	@echo Or a deployment: $(DEPLOYS)

app/secrets.yml: app/secrets.yml.m4
	$(M4) -D__ETHERIDE_EDN_BASE64__=$$(base64 "app/etheride.edn.$(ENV)") $^ > $@

app/controller.yml: app/controller.yml.m4
	$(M4) $^ > $@

app/service.yml: app/service.yml.m4
	$(M4) $^ > $@

app/create: app/secrets.yml app/controller.yml app/service.yml
	$(KUBECTL) create -f app/secrets.yml
	$(KUBECTL) create -f app/controller.yml
	$(KUBECTL) create -f app/service.yml

app/delete:
	$(KUBECTL) delete -f app/service.yml
	$(KUBECTL) delete -f app/controller.yml
	$(KUBECTL) delete -f app/secrets.yml

app/update: app/secrets.yml app/controller.yml
	@if [ -z "$(OLD_TAG)" ]; then echo "Please specify OLD_TAG="; exit 1; fi
	$(KUBECTL) update -f app/secrets.yml
	$(KUBECTL) rolling-update app-$(OLD_TAG) -f app/controller.yml

crossbar/controller.yml: crossbar/controller.yml.m4
	$(M4) $^ > $@

crossbar/service.yml: crossbar/service.yml.m4
	$(M4) $^ > $@

crossbar/create: crossbar/controller.yml crossbar/service.yml
	$(KUBECTL) create -f crossbar/controller.yml
	$(KUBECTL) create -f crossbar/service.yml

crossbar/delete:
	$(KUBECTL) delete -f crossbar/service.yml
	$(KUBECTL) delete -f crossbar/controller.yml

environment-namespace.yml: environment-namespace.yml.m4
	$(M4) $^ > $@

www-controller.yml: www-controller.yml.m4
	$(M4) $^ > $@
