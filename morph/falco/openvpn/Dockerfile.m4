FROM REPOSITORY/base:latest

EXPOSE 1194

RUN apt-get install -y openvpn iptables
# Add the config template
ADD openvpn /etc/openvpn

# Setup the service
RUN mkdir -p /etc/service/openvpn
ADD run /etc/service/openvpn/
RUN chmod 0755 /etc/service/openvpn/*

WORKDIR /etc/service/openvpn
