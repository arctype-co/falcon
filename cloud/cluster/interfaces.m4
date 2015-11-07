# This file describes the network interfaces available on your system
# and how to activate them. For more information, see interfaces(5).

# The loopback network interface
auto lo
iface lo inet loopback

auto em1
iface em1 inet static
	address 10.0.0.70
	netmask 255.255.255.0
	network 10.0.0.0
	gateway 10.0.0.1
