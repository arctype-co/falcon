#!/bin/bash

# http://kubernetes.io/v1.0/docs/getting-started-guides/ubuntu.html

# Install running as this user
FALCON_DIR=$HOME/falcon

cd $FALCON_DIR

START_DIR=`pwd`
cd cloud/cluster
cd kubernetes/cluster/ubuntu
./build.sh
cd ..
./kube-up.sh
cd ubuntu
./deployAddons.sh

cd $START_DIR

echo "Manual step! Copy ~/.docker/config.json to /root/.docker/config.json on all kubelets"
