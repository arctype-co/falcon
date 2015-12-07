kind: ReplicationController
apiVersion: v1
metadata:
  name: mysql-__TAG__
spec:
  replicas: 1
  selector:
    name: mysql-__TAG__
    role: mysql
  template:
    metadata:
      labels:
        name: mysql-__TAG__
        role: mysql
    spec:
      containers:
        - name: mysql
          image: __REPOSITORY__/mysql:__DOCKER_TAG__
          ports:
            - containerPort: 3306
          volumeMounts:
            - name: mysql-data
              mountPath: /var/lib/mysql
      volumes:
        - name: mysql-data
