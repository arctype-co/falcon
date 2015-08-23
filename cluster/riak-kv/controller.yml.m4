kind: ReplicationController
apiVersion: v1
metadata:
  name: riak-kv-__TAG__
spec:
  replicas: 1
  selector:
    name: riak-kv-__TAG__
    role: riak-kv
  template:
    metadata:
      labels:
        name: riak-kv-__TAG__
        role: riak-kv
    spec:
      containers:
        - name: riak-kv
          image: __REPOSITORY__/riak-kv:__DOCKER_TAG__
          ports:
            - containerPort: 8097
            - containerPort: 8098
            - containerPort: 8099
            - containerPort: 4369
          volumeMounts:
            - name: riak-kv-config
              mountPath: /etc/riak/secrets
              readOnly: true
      volumes:
        - name: riak-kv-config
          secret:
            secretName: riak-kv-config
