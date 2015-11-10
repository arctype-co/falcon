#!/bin/sh
SYSADMIN_PACKAGES="vim screen"
FALCON_PACKAGES="virtualbox-5.0 openjdk-7-jdk npm git"

if [ `whoami` != "root" -o "$SUDO_USER" = "" ] ;then
  "Script must be run with sudo."
fi

# https://help.ubuntu.com/community/VirtualBox/Installation
sh -c "echo 'deb http://download.virtualbox.org/virtualbox/debian '$(lsb_release -cs)' contrib non-free' > /etc/apt/sources.list.d/virtualbox.list" && wget -q http://download.virtualbox.org/virtualbox/debian/oracle_vbox.asc -O- | apt-key add -

apt-get update
echo $SYSADMIN_PACKAGES | xargs apt-get install -y 
echo $FALCON_PACKAGES | xargs apt-get install -y 

# Alias node js
rm -f /usr/bin/node
ln -s /usr/bin/nodejs /usr/bin/node

# Install vagrant
wget -nc -N https://releases.hashicorp.com/vagrant/1.7.4/vagrant_1.7.4_x86_64.deb -O /usr/local/src/
dpkg -i /usr/local/src/vagrant_1.7.4_x86_64.deb

# Install leiningen
wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein -O /usr/local/bin/lein
chown root:root /usr/local/bin/lein
chmod 0755 /usr/local/bin/lein
sudo -u $SUDO_USER lein version


# Clone falcon
if [ -d falcon ]; then
  cd falcon && sudo -u $SUDO_USER git pull
  cd -
else
  sudo -u $SUDO_USER git clone git@bitbucket.org:creeatist/falcon.git
fi

# Run make twice
sudo -u $SUDO_USER make -C falcon
sudo -u $SUDO_USER make -C falcon
