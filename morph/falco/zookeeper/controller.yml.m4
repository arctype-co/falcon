define(NAME, SERVICE-PROFILE-CONTROLLER_TAG)
kind: ReplicationController
apiVersion: v1
metadata:
  name: NAME
  labels:
    name: NAME
    role: SERVICE
    profile: PROFILE
    zk-id: "ZK_ID"
spec:
  replicas: 1 # Do NOT replicate this controller -RS
  selector:
    name: NAME
    role: SERVICE
    profile: PROFILE
    zk-id: "ZK_ID"
  template:
    metadata:
      labels:
        name: NAME
        role: SERVICE
        profile: PROFILE
        zk-id: "ZK_ID"
    spec:
      containers:
        - name: SERVICE
          image: REPOSITORY/SERVICE:CONTAINER_TAG 
          ports:
            - containerPort: 2181
            - containerPort: 2888
            - containerPort: 3888
          env:
            - name: MYID
              value: "ZK_ID"
            - name: SERVERS
              value: ZK_NODES
            - name: LOG4J_PROPERTIES_PATH
              value: /etc/service/zookeeper/secret/log4j.properties
            - name: ZOODATA
              value: /var/local/zookeeper
          volumeMounts:
            - name: SERVICE-volume-ZK_ID
              mountPath: /var/local
            - name: SERVICE-secret
              mountPath: /etc/service/zookeeper/secret
              readOnly: true
      volumes:
        - name: SERVICE-volume-ZK_ID
          hostPath:
            path: HOST_VOLUME_PATH
        - name: SERVICE-secret
          secret:
            secretName: SERVICE
