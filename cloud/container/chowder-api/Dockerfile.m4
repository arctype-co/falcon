FROM REPOSITORY/lein

EXPOSE 4501

USER app
RUN git clone git@bitbucket.org:creeatist/tender.git -b GIT_TAG
WORKDIR /home/app/tender
RUN git submodule update --init
RUN make service
ADD chowder-service.edn /home/app/tender/resources/chowder-service.edn

USER root
RUN mkdir -p /etc/service/app
ADD run /etc/service/app/run
RUN chmod 0755 /etc/service/app/run

LABEL git-tag=GIT_TAG
