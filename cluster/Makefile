.PHONY: crossbar-controller.yml
REPO=gcr.io/etheride-984
M4=m4 -D__REPOSITORY__=$(REPO)
TAG=latest

all: crossbar-controller.yml

crossbar-controller.yml: crossbar-controller.yml.m4
	$(M4) -D__TAG__=$(TAG) $^ > $@
