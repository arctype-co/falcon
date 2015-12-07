FROM REPOSITORY/jre

# Comma-separated list of hosts
ENV SERVERS zookeeper-1
# Zookeeper node id, starting at 1
ENV MYID 1
# Log4j properties
ENV LOG4J_PROPERTIES_PATH /opt/zookeeper/conf/log4j.properties
# Zookeeper config template path
ENV ZOOCFG_M4 /etc/service/zookeeper/zoo.cfg.m4
# Zookeeper data path
ENV ZOODATA /tmp/zookeeper
# Zookeeper config dir
ENV ZOOCFGDIR /etc/service/zookeeper

EXPOSE 2181 2888 3888

RUN apt-get update
RUN apt-get install -y python
# https://www.apache.org/mirrors/dist.html
RUN curl -fL http://www.us.apache.org/dist/zookeeper/stable/zookeeper-3.4.7.tar.gz | tar xzf - -C /opt && mv /opt/zookeeper-3.4.7 /opt/zookeeper

VOLUME /tmp/zookeeper
RUN mkdir -p /etc/service/zookeeper
ADD zoo.cfg.m4 /etc/service/zookeeper/
ADD run /etc/service/zookeeper/
RUN chmod 0755 /etc/service/zookeeper/run
