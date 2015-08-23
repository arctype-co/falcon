FROM __REPOSITORY__/base

# Open ports for protobuf client, HTTP client, handoff, epmd
EXPOSE 8097 8098 8099 4369

# Data volumes
VOLUME /var/lib/riak
VOLUME /var/log/riak

# Install Riak
RUN curl https://packagecloud.io/install/repositories/basho/riak/script.deb.sh | bash
RUN apt-get install -y riak=__RIAK_VERSION__

# Setup the Riak service
RUN mkdir -p /etc/service/riak
ADD run /etc/service/riak/
ADD cluster-init.sh /etc/service/riak/
ADD cluster-join.sh /etc/service/riak/
RUN chmod 0755 /etc/service/riak/*

# Add the config template
ADD riak.conf.m4 /etc/service/riak/riak.conf.m4

WORKDIR /etc/service/riak
