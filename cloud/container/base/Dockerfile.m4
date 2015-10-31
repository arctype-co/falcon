FROM phusion/baseimage:0.9.17

# Environmental variables
ENV DEBIAN_FRONTEND noninteractive

# Update apt
RUN apt-get update -q -q
RUN apt-get upgrade --yes

# Install utitiles
RUN apt-get install -y wget less screen m4

# Add ssh config
ADD ssh /root/.ssh
