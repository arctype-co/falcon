define(`SERVICE_TYPE',
    ifelse(ENVIRONMENT, `production', `LoadBalancer',
           `NodePort'))
kind: Service
apiVersion: v1
metadata:
  name: SERVICE
  labels:
    name: SERVICE
spec:
  ports:
    - port: 4501
      targetPort: 4501
      nodePort: 30501
      protocol: TCP
  selector:
    role: SERVICE
  type: SERVICE_TYPE
