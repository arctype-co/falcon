#!/bin/bash

. ./env.sh

gcloud container clusters create \
  -z $GCE_ZONE \
  --network $GCE_CLUSTER_NETWORK \
  --machine-type $GCE_MACHINE_TYPE \
  --num-numdes $GCE_CLUSTER_SIZE \
  --scopes $GCE_CLUSTER_SCOPES \
  $GCE_CLUSTER_NAME
