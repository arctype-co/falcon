kind: ReplicationController
apiVersion: v1
metadata:
  name: www.__TAG__
  labels:
    name: www.__TAG__
    role: www
spec:
  replicas: 1
  selector:
    name: www.__TAG__
    role: www
  template:
    metadata:
      labels:
        name: www.__TAG__
        role: www
    spec:
      containers:
        - name: www
          image: __REPOSITORY__/www:__DOCKER_TAG__
          ports:
            - containerPort: 3744
              hostPort: 3744
