kind: Service
apiVersion: v1
metadata:
  name: SERVICE
  labels:
    name: SERVICE
spec:
  ports:
    - port: 5432
      protocol: TCP
  selector:
    role: SERVICE
  type: ClusterIP
