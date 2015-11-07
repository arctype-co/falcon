FROM REPOSITORY/lein

EXPOSE 4501

RUN apt-get install -y make
RUN useradd -m chowder-api
WORKDIR /home/chowder-api
ADD ssh /home/chowder-api/.ssh
RUN chmod 0600 /home/chowder-api/.ssh/id_rsa*
RUN chown -R chowder-api:chowder-api /home/chowder-api
USER chowder-api
RUN git clone git@bitbucket.org:creeatist/tender.git -b GIT_TAG
WORKDIR /home/chowder-api/tender
RUN git submodule update --init
RUN make service
ADD chowder-service.edn /home/chowder-api/tender/resources/chowder-service.edn

USER root
RUN mkdir -p /etc/service/chowder-api
ADD run /etc/service/chowder-api/run
RUN chmod 0755 /etc/service/chowder-api/run

LABEL git-tag=GIT_TAG
