#!/bin/bash

riak-admin cluster join riak@$RIAK_CLUSTER_HOST
riak-admin cluster status

# If the last to join, (re)commit the cluster.
if riak-admin member-status | egrep "joining|valid" | wc -l | grep -q "$RIAK_CLUSTER_SIZE"; then
    riak-admin cluster plan && riak-admin cluster commit
fi
