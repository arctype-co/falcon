#!/bin/bash

# http://kubernetes.io/v1.0/docs/getting-started-guides/ubuntu.html

# Install running as this user
INSTALL_USER=falcon
FALCON_DIR=$HOME/falcon

export KUBE_VERSION=1.0.7
export nodes="$INSTALL_USER@10.0.0.70"
export role="ai"
export NUM_MINIONS=1
export SERVICE_CLUSTER_IP_RANGE=10.0.15.0/16
export FLANNEL_NET=172.16.0.0/16
export DOCKER_OPTS="" # extra docker cli options
export ENABLE_CLUSTER_DNS=true
export DNS_SERVER_IP=10.0.15.254
export DNS_DOMAIN="dev.chowder.us"
export DNS_REPLICAS=1
export ENABLE_CLUSTER_UI=true
export KUBERNETES_PROVIDER=ubuntu

cd $FALCON_DIR

START_DIR=`pwd`
sudo apt-get install -y golang
cd cloud/cluster
cd kubernetes/cluster/ubuntu
./build.sh
cd ..
./kube-up.sh
cd ubuntu
./deployAddons.sh

cd $START_DIR

echo "Manual step! Copy ~/.docker/config.json to /root/.docker/config.json on all kubelets"
