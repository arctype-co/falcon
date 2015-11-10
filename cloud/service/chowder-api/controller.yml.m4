kind: ReplicationController
apiVersion: v1
metadata:
  name: chowder-api.CONTROLLER_TAG
  labels:
    name: chowder-api.CONTROLLER_TAG
    role: chowder-api
spec:
  replicas: 2
  selector:
    name: chowder-api.CONTROLLER_TAG
    role: chowder-api
  template:
    metadata:
      labels:
        name: chowder-api.CONTROLLER_TAG
        role: chowder-api
    spec:
      imagePullPolicy: Always
      containers:
        - name: chowder-api
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
