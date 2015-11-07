kind: ReplicationController
apiVersion: v1
metadata:
  name: www.CONTROLLER_TAG
  labels:
    name: www.CONTROLLER_TAG
    role: www
spec:
  replicas: 1
  selector:
    name: www.CONTROLLER_TAG
    role: www
  template:
    metadata:
      labels:
        name: www.CONTROLLER_TAG
        role: www
    spec:
      containers:
        - name: www
          image: REPOSITORY/www:CONTAINER_TAG
          ports:
            - containerPort: 4000
              hostPort: 4000
