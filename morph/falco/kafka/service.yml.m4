kind: Service
apiVersion: v1
metadata:
  name: SERVICE
  labels:
    name: SERVICE
spec:
  ports:
    - port: 9092
      protocol: TCP
      name: kafka-broker
  selector:
    role: SERVICE
  type: ClusterIP
