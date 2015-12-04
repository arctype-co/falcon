FROM REPOSITORY/jre

EXPOSE 2181 2888 3888

RUN apt-get update
RUN apt-get install -y curl python
# https://www.apache.org/mirrors/dist.html
RUN curl -fL http://apache.mirror.digitalpacific.com.au/zookeeper/stable/zookeeper-3.4.7.tar.gz | tar xzf - -C /opt && mv /opt/zookeeper-3.4.7 /opt/zookeeper

VOLUME /tmp/zookeeper
RUN mkdir -p /etc/service/zookeeper
ADD run /etc/service/zookeeper/
