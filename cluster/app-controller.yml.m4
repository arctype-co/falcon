kind: ReplicationController
apiVersion: v1
metadata:
  name: app.__TAG__
  labels:
    name: app
    role: app
spec:
  replicas: 1
  selector:
    name: app.__TAG__
    role: app
  template:
    metadata:
      labels:
        name: app.__TAG__
        role: app
    spec:
      containers:
        - name: app
          image: __REPOSITORY__/app:__TAG__
          ports:
            - containerPort: 3743
              hostPort: 3743
