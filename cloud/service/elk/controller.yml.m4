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
          image: qnib/elk:CONTAINER_TAG
          ports:
            - containerPort: 9200
            - containerPort: 5514
            - containerPort: 5601
            - containerPort: 80
            - containerPort: 8500
