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
      containers:
        - name: SERVICE
          image: REPOSITORY/SERVICE:CONTAINER_TAG
          ports:
            - containerPort: 4501
          env:
            - name: CHOWDR_SERVICE_CONFIG
              value: /home/app/secret/chowdr-service.edn
          volumeMounts:
            - name: SERVICE-secret-volume
              mountPath: /home/app/secret
              readOnly: true
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
      volumes:
        - name: SERVICE-secret-volume
          secret:
            secretName: SERVICE
