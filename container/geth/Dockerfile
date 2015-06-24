# Use docker run -it --entrypoint="/usr/bin/geth" ethereum/client-go console
# docker run -p 8545:8545 -p 30303:30303 ethereum/client-go

# This Dockerfile is not used

# http://www.debian-administration.org/article/698/Automating_the_creation_of_docker_images
#   root@host~# docker build -t=etheride/runit - < Dockerfile.runit
#
#
# Launch the generated image like so,
# forwarding port 2222 on our machine to 22 in the container:
#
#   root@host~# docker run -d -p 2222:22 etheride/runit
#   
#FROM etheride/go

FROM ethereum/client-go

RUN useradd -m ethd
WORKDIR /home/ethd
ADD run.sh /home/ethd/run.sh
RUN chmod 0755 /home/ethd/run.sh
RUN mkdir /home/ethd/data
RUN mkdir /home/ethd/secrets
RUN chown -R ethd:ethd /home/ethd
USER ethd
ENTRYPOINT "/home/ethd/run.sh"
