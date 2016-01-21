#!/bin/sh
# Install falcon and it's dependencies on a Ubuntu machine

SYSADMIN_PACKAGES="vim screen htop"
KUBE_PACKAGES="golang"
FALCON_PACKAGES="openjdk-7-jdk npm git m4"

grep 14.04 /etc/issue > /dev/null 2>&1
if [ $? -ne 0 ]; then
  echo "Ubuntu version 14.04 required"
  exit 1
fi

# sudo apt-get update
echo $SYSADMIN_PACKAGES | sudo xargs apt-get install -y 
echo $KUBE_PACKAGES | sudo xargs apt-get install -y 
echo $FALCON_PACKAGES | sudo xargs apt-get install -y 

# Alias node js
sudo rm -f /usr/bin/node
sudo ln -s /usr/bin/nodejs /usr/bin/node

# Install leiningen
sudo wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein -O /usr/local/bin/lein
sudo chown root:root /usr/local/bin/lein
sudo chmod 0755 /usr/local/bin/lein
lein version

# Install docker, if not installed
docker version
if [ $? -eq 127 ]; then
  wget -qO- https://get.docker.com/ | sudo sh
fi
sudo usermod -aG docker `whoami`

# Clone falcon
if [ -d falcon ]; then
  cd falcon && git pull
  cd -
else
  git clone git@bitbucket.org:creeatist/falcon.git
fi

# Run make twice
make -C falcon
make -C falcon
