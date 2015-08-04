FROM __REPOSITORY__/lein

EXPOSE 3743

RUN apt-get install -y make npm nodejs
RUN ln -s /usr/bin/nodejs /usr/bin/node
RUN npm install -g browserify
RUN useradd -m app
WORKDIR /home/app
ADD ssh /home/app/.ssh
RUN chown -R app:app /home/app
USER app
RUN git clone git@bitbucket.org:etheride/etheride.git -b __APP_TAG__
WORKDIR /home/app/etheride
RUN git submodule update --init
RUN make clean release

USER root
RUN mkdir -p /etc/service/app
ADD run /etc/service/app/run
RUN chmod 0755 /etc/service/app/run

ADD etheride.edn /home/app/etheride/resources/etheride.edn
RUN chown app:app /home/app/etheride/resources/etheride.edn
