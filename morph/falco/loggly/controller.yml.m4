kind: ReplicationController
apiVersion: v1
metadata:
  name: SERVICE.CONTROLLER_TAG
  labels:
    name: SERVICE.CONTROLLER_TAG
    role: SERVICE
spec:
  replicas: 1
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
          image: sendgridlabs/loggly-docker:CONTAINER_TAG
          ports:
            - containerPort: 514
          env:
            - name: `TOKEN'
              value: LOGGLY_TOKEN
            - name: `TAG'
              value: syslog
