define(`SERVICE_TYPE', `ClusterIP')
kind: Service
apiVersion: v1
metadata:
  name: SERVICE
  labels:
    name: SERVICE
spec:
  ports:
    - port: 80
      targetPort: 80
      protocol: TCP
  selector:
    role: elk
  type: SERVICE_TYPE
