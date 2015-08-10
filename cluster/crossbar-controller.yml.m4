kind: ReplicationController
apiVersion: v1
metadata:
  name: crossbar-__TAG__-__ENVIRONMENT__
spec:
  replicas: 1
  selector:
    name: crossbar-__TAG__-__ENVIRONMENT__
    role: crossbar
    environment: __ENVIRONMENT__
  template:
    metadata:
      labels:
        name: crossbar-__TAG__-__ENVIRONMENT__
        role: crossbar
        environment: __ENVIRONMENT__
    spec:
      containers:
        - name: crossbar
          image: __REPOSITORY__/crossbar:__DOCKER_TAG__
          ports:
            - containerPort: 3745
              hostPort: 3745
