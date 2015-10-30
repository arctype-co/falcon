#!/bin/sh

GCE_PROJECT=chowder-1073
GCE_ZONE=us-central1-f
GCE_CLUSTER_NAME=cluster-0

gcloud config set project $GCE_PROJECT
gcloud config set compute/zone $GCE_ZONE
gcloud config set container/cluster GCE_CLUSTER_NAME

export PROJECT_ID=$GCE_CLUSTER_NAME

echo gcloud compute ssh "<node>"
