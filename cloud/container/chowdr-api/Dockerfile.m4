FROM REPOSITORY/lein

EXPOSE 4501

ENV CHOWDR_SERVICE_CONFIG "/var/run/secrets/chowdr-api/chowder-service.edn"

# Bootstrap checkout
USER app
RUN git clone git@bitbucket.org:creeatist/tender.git -b master chowdr
WORKDIR /home/app/chowdr
RUN git submodule update --init

USER root
RUN apt-get update
RUN apt-get install -y ruby-dev
RUN apt-get install -y npm
RUN make tools

# Build deployment branch
USER app
RUN git fetch -t origin GIT_TAG
RUN git checkout GIT_TAG
RUN git submodule update
RUN git log -n 1
RUN make service || make service

USER root
RUN mkdir -p /etc/service/app
ADD run /etc/service/app/run
RUN chmod 0755 /etc/service/app/run

LABEL git-tag=GIT_TAG
