FROM etheride/base

WORKDIR /usr/local/src
RUN wget https://storage.googleapis.com/golang/go1.4.2.linux-amd64.tar.gz 
# Install to /usr/local/go, default GOROOT
RUN tar -C /usr/local -xzf go1.4.2.linux-amd64.tar.gz 
RUN ln -s /usr/local/go/bin/* /usr/local/bin/
