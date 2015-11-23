kind: ReplicationController
apiVersion: v1
metadata:
  name: SERVICE-CONTROLLER_TAG
spec:
  replicas: REPLICAS
  selector:
    name: SERVICE-CONTROLLER_TAG
    role: SERVICE
  template:
    metadata:
      labels:
        name: SERVICE-CONTROLLER_TAG
        role: SERVICE
    spec:
      containers:
        - name: SERVICE
          image: REPOSITORY/SERVICE:CONTAINER_TAG
          ports:
            - containerPort: 8087
            - containerPort: 8098
            - containerPort: 8099
            - containerPort: 4369
          env:
            - name: `KUBE_NAMESPACE'
              value: "ENVIRONMENT"
            - name: `RIAK_CLUSTER_SIZE'
              value: "RIAK_CLUSTER_SIZE"
