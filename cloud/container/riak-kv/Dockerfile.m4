FROM REPOSITORY/base

# Open ports for protobuf client, HTTP client, handoff, epmd
EXPOSE 8087 8098 8099 4369

# Data volumes
VOLUME /var/lib/riak
VOLUME /var/log/riak

# Install Riak
RUN curl https://packagecloud.io/gpg.key | sudo apt-key add -
RUN apt-get install -y apt-transport-https
ADD basho.list /etc/apt/sources.list.d/basho.list
RUN apt-get update
RUN apt-get install -y riak=RIAK_VERSION

# Install jq
RUN apt-get install -y jq

# Setup the Riak service
RUN mkdir -p /etc/service/riak
ADD run /etc/service/riak/
ADD cluster-join.sh /etc/service/riak/
RUN chmod 0755 /etc/service/riak/*

# Add the config template
ADD riak.conf.m4 /etc/service/riak/riak.conf.m4

WORKDIR /etc/service/riak
