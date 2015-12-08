FROM REPOSITORY/pypy

RUN apt-get install -y libssl-dev libffi-dev
RUN useradd -m crossbar

WORKDIR /home/crossbar
RUN virtualenv --python=pypy .; . bin/activate; pip install crossbar

ADD config /home/crossbar/.crossbar
RUN chown -R crossbar:crossbar /home/crossbar

RUN mkdir -p /etc/service/crossbar
ADD run /etc/service/crossbar/run
RUN chmod 0755 /etc/service/crossbar/run
