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
      nodePort: 31194
      protocol: TCP
  selector:
    role: SERVICE
  type: NodePort
