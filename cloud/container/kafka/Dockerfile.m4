FROM REPOSITORY/jre

EXPOSE 9092

ENV LOG4J_PROPERTIES_PATH /usr/local/kafka/config/log4j.properties
ENV SERVER_PROPERTIES_M4_PATH /etc/service/kafka/server.properties.m4
ENV BROKER_ID 0
ENV ZOOKEEPER_CONNECT localhost:2181
ENV KAFKA_LOG_DIRS /tmp/kafka-logs
ENV KAFKA_HEAP_OPTS "-Xmx1G -Xms1G"

WORKDIR /usr/local/kafka
RUN curl -fSL http://www.us.apache.org/dist/kafka/0.9.0.0/kafka_2.11-0.9.0.0.tgz | tar -xz --strip-components=1

RUN mkdir -p /etc/service/kafka
ADD run /etc/service/kafka/
RUN chmod 0755 /etc/service/kafka/run
