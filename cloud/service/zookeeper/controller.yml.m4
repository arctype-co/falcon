kind: ReplicationController
apiVersion: v1
metadata:
  name: SERVICE-CONTROLLER_TAG
  labels:
    name: SERVICE-CONTROLLER_TAG
    role: SERVICE
    zk-id: ZK_ID
spec:
  replicas: 1 # Do NOT replicate this controller -RS
  selector:
    name: SERVICE-CONTROLLER_TAG
    role: SERVICE
    zk-id: ZK_ID
  template:
    metadata:
      labels:
        name: SERVICE-CONTROLLER_TAG
        role: SERVICE
        zk-id: ZK_ID
    spec:
      containers:
        - name: SERVICE
          image: mesoscloud/zookeeper:CONTAINER_TAG 
          ports:
            - containerPort: 2181
            - containerPort: 2888
            - containerPort: 3888
          env:
            - name: MYID
              value: ZK_ID
            - name: SERVERS
              value: ZK_NODES
              
            
