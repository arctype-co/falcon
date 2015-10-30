FROM gcr.io/etheride-984/base

# Open MySQL server port
EXPOSE 3306

# Data volumes
VOLUME /var/lib/mysql

RUN apt-get -y install mysql-server

ADD run /etc/service/mysql/
RUN chmod 0755 /etc/service/mysql/*
