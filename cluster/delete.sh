#!/bin/bash

. ./env.sh

gcloud clusters delete \
  -z $GCE_ZONE \
  $GCE_CLUSTER_NAME
