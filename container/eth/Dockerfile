FROM etheride/base

WORKDIR /usr/local/src

RUN apt-get -y install build-essential git cmake libboost-all-dev libgmp-dev libleveldb-dev libminiupnpc-dev libreadline-dev libncurses5-dev libcurl4-openssl-dev libmicrohttpd-dev libjsoncpp-dev libargtable2-dev llvm-3.6-dev libedit-dev mesa-common-dev ocl-icd-libopencl1 opencl-headers libgoogle-perftools-dev
# RUN apt-get -y install libcryptopp-dev libjson-rpc-cpp-dev 

# Install libjson-rpc-cpp
RUN wget https://github.com/cinemast/libjson-rpc-cpp/archive/v0.5.0.tar.gz
RUN tar xzf v0.5.0.tar.gz
RUN mkdir -p libjson-rpc-cpp-0.5.0/build
WORKDIR libjson-rpc-cpp-0.5.0/build
RUN cmake ..
RUN make
RUN make install
RUN ldconfig

# Install Crypto++
WORKDIR /usr/local/src
RUN apt-get -y install unzip
RUN wget http://www.cryptopp.com/cryptopp562.zip
RUN unzip -d cryptopp cryptopp562.zip
WORKDIR cryptopp
RUN make -e CXXFLAGS="-DNDEBUG -g -O2 -fPIC"
RUN make install
RUN ldconfig

# Install cpp-ethereum
ENV ETH_BRANCH "develop"
WORKDIR /usr/local/src
RUN git clone -b $ETH_BRANCH https://github.com/ethereum/cpp-ethereum
WORKDIR cpp-ethereum
RUN mkdir build
WORKDIR build
RUN cmake .. -DETHASHCL=0 -DGUI=0 -DCMAKE_BUILD_TYPE=Release
RUN make -j3
RUN make install
RUN ldconfig

# Clean src
RUN rm -rf /usr/local/src/*

WORKDIR /
