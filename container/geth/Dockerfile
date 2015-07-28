FROM etheride/eth

# Install go
WORKDIR /usr/local/src/
RUN wget -q https://storage.googleapis.com/golang/go1.4.2.linux-amd64.tar.gz
RUN tar -C /usr/local -xzf go1.4.2.linux-amd64.tar.gz
ENV PATH "$PATH:/usr/local/go/bin"
RUN go version

# Install geth
RUN apt-get install -y build-essential libgmp3-dev
RUN git clone https://github.com/ethereum/go-ethereum -b v0.9.38
WORKDIR go-ethereum
RUN make geth
RUN cp build/bin/geth /usr/local/bin/
RUN geth version

# Runtime env
RUN mkdir -p /etc/service/geth
ADD run /etc/service/geth/run
RUN chmod 0755 /etc/service/geth/run
RUN useradd -m ethd
ENV ETHD_DATA "/home/ethd/volume/data"
ENV ETHD_SECRETS "/home/ethd/secrets"
RUN mkdir -p $ETHD_DATA
RUN mkdir -p $ETHD_SECRETS 
RUN chown -R ethd:ethd /home/ethd

# Clean src
RUN rm -rf /usr/local/src/*

WORKDIR /
