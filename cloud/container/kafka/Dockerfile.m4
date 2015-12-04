FROM REPOSITORY/jre

EXPOSE 9092

ENV KAFKA_SERVER_PROPERTIES_PATH /usr/local/kafka/config/server.properties
ENV KAFKA_LOG4J_PROPERTIES_PATH /usr/local/kafka/config/log4j.properties
# From kafka-server-start.sh
ENV KAFKA_LOG4J_OPTS "-Dlog4j.configuration=file:${KAFKA_LOG4J_PROPERTIES_PATH}"
ENV KAFKA_HEAP_OPTS "-Xmx1G -Xms1G"

WORKDIR /usr/local/kafka
RUN curl -fSL http://www.us.apache.org/dist/kafka/0.9.0.0/kafka_2.11-0.9.0.0.tgz | tar -xz --strip-components=1

RUN mkdir -p /etc/service/kafka
ADD run /etc/service/kafka/
RUN chmod 0755 /etc/service/kafka/run
