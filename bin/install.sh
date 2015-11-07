#!/bin/sh
PACKAGES=virtualbox-5.0 openjdk-8-jdk npm git

apt-get install -y $PACKAGES

# Alias node js
ln -s /usr/bin/nodejs /usr/bin/node

# Install vagrant
wget https://releases.hashicorp.com/vagrant/1.7.4/vagrant_1.7.4_x86_64.deb
dpkg -i vagrant_1.7.4_x86_64.deb
