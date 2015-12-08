# Addresses in this subnet
server VPN_VIRTUAL_NETWORK VPN_VIRTUAL_MASK

verb 3
key /etc/openvpn/pki/private/vpn.etheride.com.key
ca /etc/openvpn/pki/ca.crt
cert /etc/openvpn/pki/issued/vpn.etheride.com.crt
dh /etc/openvpn/pki/dh.pem
tls-auth /etc/openvpn/pki/ta.key
key-direction 0
keepalive 10 60
persist-key
persist-tun

proto tcp
port 1194
dev tun0
status /tmp/openvpn-status.log

# client-config-dir /etc/openvpn/ccd

user nobody
group nogroup

#push dhcp-option DNS 8.8.4.4
#push dhcp-option DNS 8.8.8.8

# Route client subnets
# common home/office networks
route 192.168.0.0 255.255.255.0
route 10.0.0.0 255.255.0.0
# Route server subnets
push "route VPN_SUBNET_A_NETWORK VPN_SUBNET_A_MASK"
push "route VPN_SUBNET_B_NETWORK VPN_SUBNET_B_MASK"

#up "/etc/openvpn/up.sh br0"
#down "/etc/openvpn/down.sh br0"
