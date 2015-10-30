#!/bin/sh

GCE_PROJECT=chowder-1073
GCE_ZONE=us-central1-f
GCE_CLUSTER_MACHINE_TYPE=n1-standard
GCE_CLUSTER_NAME=cluster-0
GCE_CLUSTER_NETWORK=10.10.0.0/14 
GCE_CLUSTER_SCOPES=logging-write,datastore,monitoring,sql,sql-admin,storage-rw
GCE_CLUSTER_SIZE=3

gcloud config set project $GCE_PROJECT
gcloud config set compute/zone $GCE_ZONE
gcloud config set container/cluster $GCE_CLUSTER_NAME

export PROJECT_ID=$GCE_CLUSTER_NAME
