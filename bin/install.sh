#!/bin/sh
SYSADMIN_PACKAGES="vim screen htop"
FALCON_PACKAGES="virtualbox-5.0 openjdk-7-jdk npm git"

# https://help.ubuntu.com/community/VirtualBox/Installation
sudo sh -c "echo 'deb http://download.virtualbox.org/virtualbox/debian '$(lsb_release -cs)' contrib non-free' > /etc/apt/sources.list.d/virtualbox.list" && wget -q http://download.virtualbox.org/virtualbox/debian/oracle_vbox.asc -O- | sudo apt-key add -

# sudo apt-get update
echo $SYSADMIN_PACKAGES | sudo xargs apt-get install -y 
echo $FALCON_PACKAGES | sudo xargs apt-get install -y 

# Alias node js
sudo rm -f /usr/bin/node
sudo ln -s /usr/bin/nodejs /usr/bin/node

# Install vagrant
sudo wget -nc -N https://releases.hashicorp.com/vagrant/1.7.4/vagrant_1.7.4_x86_64.deb -O /usr/local/src/
sudo dpkg -i /usr/local/src/vagrant_1.7.4_x86_64.deb

# Install leiningen
sudo wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein -O /usr/local/bin/lein
sudo chown root:root /usr/local/bin/lein
sudo chmod 0755 /usr/local/bin/lein
lein version

# Install docker
wget -qO- https://get.docker.com/ | sudo sh
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
