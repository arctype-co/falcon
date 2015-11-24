# Port Map

## Infras

22 | ssh | sshd
1194 | openvpn | openvpn gateway

## Applications
port | protocol | description
514  | rsyslog | syslogd
4000 | http | www.chowder.us
4001 | etcd | CoreOS etcd
4501 | http | Chowdr API service
4514 | rsyslog | Loggly syslogd
30501 | http | Chowder API service node port
31194 | openvpn | openvpn node port

9200 | elk | ELK stack
5514 | elk | ELK stack
5601 | elk | ELK stack
80 | elk | ELK stack
8500 | elk | ELK stack

## Riak
4369 | epmd | Erlang Port Mapper Daemon
8087 | protobuf | Riak Protobuf
8093 | solr | Riak Solr Search
8098 | http | Riak HTTP
8099 | riak | Riak handoff
8985 | jmx | Riak Solr JMX

