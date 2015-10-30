FROM __REPOSITORY__/lein

EXPOSE 3744

RUN apt-get install -y make
RUN useradd -m www
WORKDIR /home/www
ADD ssh /home/www/.ssh
RUN chown -R www:www /home/www

USER www
RUN git clone git@bitbucket.org:etheride/etheride.com.git -b __WWW_TAG__
WORKDIR /home/www/etheride.com
RUN make

USER root
RUN mkdir -p /etc/service/www
ADD run /etc/service/www/run
RUN chmod 0755 /etc/service/www/run
