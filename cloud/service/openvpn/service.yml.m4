kind: Service
apiVersion: v1
metadata:
  name: SERVICE
  labels:
     name: SERVICE
spec:
  ports:
    - port: 1194
      targetPort: 1194
      protocol: TCP
  selector:
    name: SERVICE
  type: LoadBalancer
