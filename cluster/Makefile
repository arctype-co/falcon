REPO=gcr.io/etheride-984
M4=m4 -D__REPOSITORY__=$(REPO)
TAG=latest
CONTROLLERS=app-controller.yml crossbar-controller.yml

.PHONY: $(CONTROLLERS)

all: $(CONTROLLERS)

app-controller.yml: app-controller.yml.m4
	$(M4) -D__TAG__=$(TAG) $^ > $@

crossbar-controller.yml: crossbar-controller.yml.m4
	$(M4) -D__TAG__=$(TAG) $^ > $@
