REPO=creeatist
# development, production
ENV=development
M4=m4 
KUBECTL=kubectl --namespace=$(ENV)
CONFIGS=environment/namespace.yml

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

riak-kv/controller.yml: riak-kv/controller.yml.m4
	$(M4) $^ > $@

riak-kv/secrets.yml: riak-kv/secrets.yml.m4
	$(M4) \
	 	-DCLUSTER_HOST_BASE64=$$(base64 <<< `${call FIRST_POD_ROLE_IP, "riak-kv"}`) \
		-DCLUSTER_SIZE_BASE64=$$(base64 <<< $(RIAK_CLUSTER_SIZE)) \
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
