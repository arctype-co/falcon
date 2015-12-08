FROM phusion/baseimage:0.9.17

# Environmental variables
ENV DEBIAN_FRONTEND noninteractive

# Update apt
RUN apt-get update -q -q
RUN apt-get upgrade --yes

# Install utitiles
RUN apt-get install -y wget less screen m4 make git

# Configure syslog
ADD syslog-ng.conf /etc/syslog-ng/syslog-ng.conf
ADD loggly.conf /etc/syslog-ng/conf.d/loggly.conf

RUN useradd -m app
# Add ssh config
ADD ssh /home/app/.ssh
RUN chmod 0600 /home/app/.ssh/id_rsa*
# Mount secrets here
RUN mkdir /home/app/secret/
WORKDIR /home/app
RUN chown -R app:app /home/app
