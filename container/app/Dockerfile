FROM etheride/lein
MAINTAINER ryan.sundberg@gmail.com

EXPOSE 3743

RUN apt-get install make
RUN useradd -m app
ADD etheride.tar.xz /home/app
WORKDIR /home/app/etheride
RUN chown -R app:app /home/app
USER app
RUN make clean release

USER root
RUN mkdir -p /etc/service/app
ADD run /etc/service/app/run
RUN chmod 0755 /etc/service/app/run
