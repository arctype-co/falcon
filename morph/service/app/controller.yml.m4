kind: ReplicationController
apiVersion: v1
metadata:
  name: app-__TAG__
spec:
  replicas: 1
  selector:
    name: app-__TAG__
    role: app
  template:
    metadata:
      labels:
        name: app-__TAG__
        role: app
    spec:
      containers:
        - name: app
          image: __REPOSITORY__/app:__DOCKER_TAG__
          ports:
            - containerPort: 3743
              hostPort: 3743
          volumeMounts:
            - name: app-config
              mountPath: /home/app/secrets
              readOnly: true
      volumes:
        - name: app-config
          secret:
            secretName: app-config
