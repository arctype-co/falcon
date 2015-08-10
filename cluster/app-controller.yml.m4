kind: ReplicationController
apiVersion: v1
metadata:
  name: app-__TAG__-__ENVIRONMENT__
spec:
  replicas: 1
  selector:
    name: app-__TAG__-__ENVIRONMENT__
    role: app
    environment: __ENVIRONMENT__
  template:
    metadata:
      labels:
        name: app-__TAG__-__ENVIRONMENT__
        role: app
        environment: __ENVIRONMENT__
    spec:
      containers:
        - name: app
          image: __REPOSITORY__/app:__DOCKER_TAG__
          ports:
            - containerPort: 3743
              hostPort: 3743
