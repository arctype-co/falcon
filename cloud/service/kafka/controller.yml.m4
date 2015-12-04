define(NAME, SERVICE-PROFILE-CONTROLLER_TAG)
kind: ReplicationController
apiVersion: v1
metadata:
  name: NAME
  labels:
    name: NAME
    role: SERVICE
    profile: PROFILE
spec:
  replicas: 1 # Do NOT replicate this controller -RS
  selector:
    name: NAME
    role: SERVICE
    profile: PROFILE
  template:
    metadata:
      labels:
        name: NAME
        role: SERVICE
        profile: PROFILE
    spec:
      containers:
        - name: SERVICE
          image: REPOSITORY/SERVICE:CONTAINER_TAG 
          ports:
            - containerPort: 9092
          env:
            - name: `BROKER_ID'
              value: "BROKER_ID"
            - name: `ZOOKEEPER_CONNECT'
              value: ZOOKEEPER_CONNECT
            - name: KAFKA_LOG_DIRS
              value: /var/local/kafka
            - env: LOG4J_PROPERTIES_PATH
              value: /etc/service/kafka/secret/log4j.properties
          volumeMounts:
            - name: SERVICE-volume-BROKER_ID
              mountPath: /var/local
            - name: SERVICE-secret
              mountPath: /etc/service/kafka/secret
              readOnly: true
      volumes:
        - name: SERVICE-volume-BROKER_ID
          hostPath:
            path: HOST_VOLUME_PATH
        - name: SERVICE-secret
          secret:
            secretName: SERVICE
