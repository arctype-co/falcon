# Port Map

port | protocol | description
## Infras
22 | ssh | sshd
514 | rsyslog | syslogd
4001 | etcd | etcd (Kubernetes)
1194 | openvpn | openvpn gateway
31194 | openvpn | openvpn node port
8080 | http | Kubernetes API
## Riak
4369 | epmd | Erlang Port Mapper Daemon
8087 | protobuf | Riak Protobuf
8093 | solr | Riak Solr Search
8098 | http | Riak HTTP
8099 | riak | Riak handoff
8985 | jmx | Riak Solr JMX
## Zookeeper
2181 | zk | Zookeeper client
2888 | zk-follow | Zookeeper follower
3888 | zk-elect | Zookeeper election
## Kafka
9092 | kafka | Kafka broker
## Postgres
5432 | psql | PostgreSQL
