kind: Service
apiVersion: v1
metadata:
  name: SERVICE
  labels:
    name: SERVICE
spec:
  ports:
    - port: 8087
      targetPort: 8087
      protocol: TCP
      name: protobuf-client
    - port: 8098
      targetPort: 8098
      protocol: TCP
      name: http-client
  selector:
    role: SERVICE
  type: ClusterIP
