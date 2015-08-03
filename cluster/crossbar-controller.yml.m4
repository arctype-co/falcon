kind: ReplicationController
apiVersion: v1
metadata:
  name: crossbar.__TAG__
  labels:
    name: crossbar
    role: crossbar
spec:
  replicas: 1
  selector:
    name: crossbar.__TAG__
    role: crossbar
  template:
    metadata:
      labels:
        name: crossbar.__TAG__
        role: crossbar
    spec:
      containers:
        - name: crossbar
          image: __REPOSITORY__/crossbar:__TAG__
          ports:
            - containerPort: 3745
              hostPort: 3745
