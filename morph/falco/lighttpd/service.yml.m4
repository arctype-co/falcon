define(`SERVICE_TYPE',
    ifelse(ENVIRONMENT, `production', `LoadBalancer',
           `ClusterIP'))
kind: Service
apiVersion: v1
metadata:
  name: SERVICE.CONTROLLER_TAG
  labels:
    name: SERVICE
spec:
  ports:
    - port: 80
      targetPort: 8080
      protocol: TCP
  selector:
    role: SERVICE
  type: SERVICE_TYPE
