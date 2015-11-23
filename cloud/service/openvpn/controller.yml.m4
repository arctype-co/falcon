# Requires privileged execution
kind: ReplicationController
apiVersion: v1
metadata:
  name: SERVICE.CONTROLLER_TAG
  labels:
    name: SERVICE.CONTROLLER_TAG
    role: SERVICE
spec:
  replicas: REPLICAS
  selector:
    name: SERVICE.CONTROLLER_TAG
    role: SERVICE
  template:
    metadata:
      labels:
        name: SERVICE.CONTROLLER_TAG
        role: SERVICE
    spec:
      containers:
        - name: SERVICE
          image: REPOSITORY/openvpn:CONTAINER_TAG
          ports:
            - containerPort: 1194
              hostPort: 1194
          securityContext:
            privileged: true
          env:
            - name: `VPN_VIRTUAL_NETWORK'
              value: "VPN_VIRTUAL_NETWORK"
            - name: `VPN_VIRTUAL_MASK'
              value: "VPN_VIRTUAL_MASK"
            - name: `VPN_VIRTUAL_NAT'
              value: "VPN_VIRTUAL_NAT"
            - name: `VPN_SUBNET_A_NETWORK'
              value: "VPN_SUBNET_A_NETWORK"
            - name: `VPN_SUBNET_A_MASK'
              value: "VPN_SUBNET_A_MASK"
            - name: `VPN_SUBNET_B_NETWORK'
              value: "VPN_SUBNET_B_NETWORK"
            - name: `VPN_SUBNET_B_MASK'
              value: "VPN_SUBNET_B_MASK"

