REPO=creeatist
# development, production
ENV=development
CONTROLLER_TAG=latest
CONTAINER_TAG=latest
M4=m4 -D__REPOSITORY__=$(REPO) -D__TAG__=$(CONTROLLER_TAG) -D__DOCKER_TAG__=$(CONTAINER_TAG) -D__ENVIRONMENT__=$(ENV)
KUBECTL=kubectl --namespace=$(ENV)
CONFIGS=app/controller.yml app/secrets.yml app/service.yml crossbar/controller.yml crossbar/service.yml environment/namespace.yml riak-kv/controller.yml riak-kv/secrets.yml riak-kv/service.yml www/controller.yml www/service.yml chowder-api/controller.yml chowder-api/service.yml
DEPLOYS=app/create app/delete app/update crossbar/create crossbar/delete environment/create riak-kv/create-node

# App specific
RIAK_CLUSTER_SIZE=5

.PHONY: $(CONFIGS) logs pods rc secrets services

ANNOUNCE=@../../bin/post.js `whoami` "$(MAKECMDGOALS) $(MAKEFLAGS)"
FIRST_POD_ROLE_IP=$(KUBECTL) get pods -o json | jq -r '.items | map(select(.metadata.labels.role == $(1))) | .[0].status.podIP | select(. != null)'
CHECK_POD_ROLE_READY=$(KUBECTL) get pods -o json | jq '.items | map(select(.metadata.labels.role == $(1))) | map(.status.phase == "Running") | any'
WAIT_FOR_POD_ROLE=@echo Waiting for role $(1); while [ `${call CHECK_POD_ROLE_READY, $(1)}` != "true" ]; do /bin/echo -n .; sleep 1; done

announce: 
	${call ANNOUNCE}

all: 
	@echo Please specify a configuration: $(CONFIGS)
	@echo Or a deployment: $(DEPLOYS)

logs:
	$(KUBECTL) logs $(NAME)

pods:
	$(KUBECTL) get pods

pod-status:
	$(KUBECTL) get pods -o json | jq '.items | map(select(.metadata.name == "$(NAME)")) | .[0].status'

rc:
	$(KUBECTL) get rc

secrets:
	$(KUBECTL) get secrets

services:
	$(KUBECTL) get services

%/controller.yml:
	$(M4) $*/controller.yml.m4 > $@

%/service.yml:
	$(M4) $*/service.yml.m4 > $@

app/secrets.yml: app/secrets.yml.m4
	$(M4) -D__ETHERIDE_EDN_BASE64__=$$(base64 "app/etheride.edn.$(ENV)") $^ > $@

app/controller.yml: app/controller.yml.m4
	$(M4) $^ > $@

app/create: app/secrets.yml app/controller.yml app/service.yml | announce
	$(KUBECTL) create -f app/secrets.yml
	$(KUBECTL) create -f app/controller.yml
	$(KUBECTL) create -f app/service.yml

app/delete: | announce
	$(KUBECTL) delete -f app/service.yml
	$(KUBECTL) delete -f app/controller.yml
	$(KUBECTL) delete -f app/secrets.yml

app/update: app/secrets.yml app/controller.yml | announce
	@if [ -z "$(OLD_TAG)" ]; then echo "Please specify OLD_TAG="; exit 1; fi
	$(KUBECTL) update -f app/secrets.yml
	$(KUBECTL) rolling-update app-$(OLD_TAG) -f app/controller.yml

crossbar/controller.yml: crossbar/controller.yml.m4
	$(M4) $^ > $@

crossbar/create: crossbar/controller.yml crossbar/service.yml | announce
	$(KUBECTL) create -f crossbar/controller.yml
	$(KUBECTL) create -f crossbar/service.yml

crossbar/delete: | announce
	$(KUBECTL) delete -f crossbar/service.yml
	$(KUBECTL) delete -f crossbar/controller.yml

environment/namespace.yml: environment/namespace.yml.m4
	$(M4) $^ > $@

riak-kv/controller.yml: riak-kv/controller.yml.m4
	$(M4) $^ > $@

riak-kv/secrets.yml: riak-kv/secrets.yml.m4
	$(M4) \
	 	-D__CLUSTER_HOST_BASE64__=$$(base64 <<< `${call FIRST_POD_ROLE_IP, "riak-kv"}`) \
		-D__CLUSTER_SIZE_BASE64__=$$(base64 <<< $(RIAK_CLUSTER_SIZE)) \
	 	$^ > $@

riak-kv/create-cluster: riak-kv/controller.yml riak-kv/service.yml riak-kv/secrets.yml | announce
	$(KUBECTL) create -f riak-kv/secrets.yml
	$(KUBECTL) create -f riak-kv/service.yml
	$(KUBECTL) create -f riak-kv/controller.yml
	${call WAIT_FOR_POD_ROLE, "riak-kv"}
	make riak-kv/secrets.yml $(MAKEFLAGS)
	$(KUBECTL) update -f riak-kv/secrets.yml
	$(KUBECTL) scale --replicas=$(RIAK_CLUSTER_SIZE) rc riak-kv-$(TAG)
	$(KUBECTL) get pods

riak-kv/delete-cluster: | announce
	$(KUBECTL) delete -f riak-kv/controller.yml
	$(KUBECTL) delete -f riak-kv/service.yml
	$(KUBECTL) delete -f riak-kv/secrets.yml

www/controller.yml: www/controller.yml.m4
	$(M4) $^ > $@
