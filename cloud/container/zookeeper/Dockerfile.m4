FROM REPOSITORY/jre

# Comma-separated list of hosts
ENV SERVERS zookeeper-1
# Zookeeper node id, starting at 1
ENV MYID 1

EXPOSE 2181 2888 3888

RUN apt-get update
RUN apt-get install -y curl python
# https://www.apache.org/mirrors/dist.html
RUN curl -fL http://www.us.apache.org/dist/zookeeper/stable/zookeeper-3.4.7.tar.gz | tar xzf - -C /opt && mv /opt/zookeeper-3.4.7 /opt/zookeeper

VOLUME /tmp/zookeeper
RUN mkdir -p /etc/service/zookeeper
ADD run /etc/service/zookeeper/
