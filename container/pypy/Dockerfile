FROM etheride/base

WORKDIR /usr/local/src
RUN wget https://bitbucket.org/pypy/pypy/downloads/pypy-2.6.0-linux64.tar.bz2
RUN tar -C /usr/local -xjf pypy-2.6.0-linux64.tar.bz2
RUN ln -s /usr/local/pypy-2.6.0-linux64/bin/pypy /usr/local/bin
RUN rm /usr/local/src/*
WORKDIR /
RUN pypy -V
RUN apt-get install -y python-pip python-dev
RUN pip install virtualenv
