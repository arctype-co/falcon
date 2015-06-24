# http://www.debian-administration.org/article/698/Automating_the_creation_of_docker_images
# Simple dockerfile for an ssh + server.
#
# Build it like so:
#
#   root@host~# docker build -t=etheride/base - < Dockerfile.base
#
# Launch the generated image like so,
# forwarding port 8888 on our machine to 80 in the container:
#
#   root@host~# docker run -d -p 8888:80 etheride/base
#

#  From this base-image / starting-point
FROM debian

#  Authorship
MAINTAINER ryan.sundberg@gmail.com

# Update apt
RUN apt-get update -q -q
RUN apt-get upgrade --yes

# Install utitiles
RUN apt-get install -y wget less

# Install runit
# RUN apt-get install runit --yes --force-yes

# Finally launch runit.
# ENTRYPOINT ["/usr/sbin/runsvdir-start"]
