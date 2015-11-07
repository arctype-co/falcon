# Requires privileged execution
kind: ReplicationController
apiVersion: v1
metadata:
  name: SERVICE
  labels:
    name: SERVICE
spec:
  replicas: 1
  selector:
    name: SERVICE
  template:
    metadata:
      labels:
        name: SERVICE
    spec:
      containers:
        - name: SERVICE
          image: REPOSITORY/openvpn:CONTAINER_TAG
          ports:
            - containerPort: 1194
              hostPort: 1194
          privileged: true
