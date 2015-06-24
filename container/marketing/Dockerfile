# http://www.debian-administration.org/article/698/Automating_the_creation_of_docker_images
# Simple dockerfile for an ssh + server.
#
# Build it like so:
#
#   root@host~# docker build -t=etheride/runit - < Dockerfile.runit
#
# Launch the generated image like so,
# forwarding port 2222 on our machine to 22 in the container:
#
#   root@host~# docker run -d -p 2222:22 etheride/runit
#

#  From this base-image / starting-point
FROM etheride/lein

#  Authorship
MAINTAINER ryan.sundberg@gmail.com

EXPOSE 3744

RUN useradd -m marketing
WORKDIR /home/marketing/etheride
ADD marketing.tar.xz /home/marketing/etheride
ENV ARCHIVA_USERNAME=read
ENV ARCHIVA_PASSWORD="gh49qKL*8zgsrOSohe"
RUN lein deps
RUN chown -R marketing:marketing /home/marketing

USER marketing
CMD ["lein", "run", "-m", "etheride.marketing-server"]
