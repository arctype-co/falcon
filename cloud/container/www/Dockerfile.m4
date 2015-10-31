FROM __REPOSITORY__/base

EXPOSE 4000

RUN apt-get install -y lighttpd
ADD lighttpd.conf /etc/lighttpd/lighttpd.conf
ADD index.html /var/www/index.html
RUN chown www-data:www-data /var/www/index.html

RUN mkdir -p /etc/service/www
ADD run /etc/service/www/run
RUN chmod 0755 /etc/service/www/run
