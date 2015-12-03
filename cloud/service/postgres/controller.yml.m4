kind: ReplicationController
apiVersion: v1
metadata:
  name: SERVICE.CONTROLLER_TAG
  labels:
    name: SERVICE.CONTROLLER_TAG
    role: SERVICE
spec:
  replicas: 1 # Do NOT replicate this controller -RS
  selector:
    name: SERVICE.CONTROLLER_TAG
    role: SERVICE
  template:
    metadata:
      labels:
        name: SERVICE.CONTROLLER_TAG
        role: SERVICE
    spec:
      containers:
        - name: SERVICE
          image: postgres:CONTAINER_TAG
          ports:
            - containerPort: 5432 # Postgres
          env: 
            - name: PGDATA
              value: /var/lib/postgresql/data/pgdata
            - name: `POSTGRES_USER' # Superuser
              value: postgres
            - name: `POSTGRES_PASSWORD' # Superuser password
              value: POSTGRES_PASSWORD
          volumeMounts:
            - name: SERVICE-volume-0
              mountPath: /var/lib/postgresql/data
      volumes:
        - name: SERVICE-volume-0
          hostPath:
            path: HOST_VOLUME_PATH
