kind: ReplicationController
apiVersion: v1
metadata:
  name: SERVICE.CONTROLLER_TAG
  labels:
    name: SERVICE.CONTROLLER_TAG
    role: SERVICE
spec:
  replicas: 2
  selector:
    name: SERVICE.CONTROLLER_TAG
    role: SERVICE
  template:
    metadata:
      labels:
        name: SERVICE.CONTROLLER_TAG
        role: SERVICE
    spec:
      imagePullPolicy: Always
      containers:
        - name: SERVICE
          image: REPOSITORY/chowder-api:CONTAINER_TAG
          ports:
            - containerPort: 4501
      livenessProbe:
            httpGet:
              path: /api/health
              port: 4501
            initialDelaySeconds: 30 
            timeoutSeconds: 1
      readinessProbe:
            httpGet:
              path: /api/health
              port: 4501
