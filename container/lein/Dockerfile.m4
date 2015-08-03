#  From this base-image / starting-point
FROM __REPOSITORY__/jre

# Share lein 
ENV LEIN_HOME=/usr/local/share/lein

# Install lein
RUN wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein -O /usr/local/bin/lein
RUN chmod 0755 /usr/local/bin/lein
RUN LEIN_ROOT=true lein version

# Install git
RUN apt-get install -y git
