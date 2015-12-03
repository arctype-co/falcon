kind: ReplicationController
apiVersion: v1
metadata:
  name: SERVICE.CONTROLLER_TAG
  labels:
    name: SERVICE.CONTROLLER_TAG
    role: SERVICE
spec:
  replicas: 1 # Do NOT replicate this controller -RS
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
          image: spotify/kafka:CONTAINER_TAG
          ports:
            - containerPort: 2181 # Zookeeper
            - containerPort: 9092 # Kafka broker
          volumeMounts:
            - name: SERVICE-volume-0
              mountPath: /tmp # where this image stores zk/kafka data
      volumes:
        - name: SERVICE-volume-0
          hostPath:
            path: HOST_VOLUME_PATH
