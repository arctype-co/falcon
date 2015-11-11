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

9200 | elk | ELK stack
5514 | elk | ELK stack
5601 | elk | ELK stack
80 | elk | ELK stack
8500 | elk | ELK stack

