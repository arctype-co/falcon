kind: Service
apiVersion: v1
metadata:
  name: SERVICE
  labels:
    name: SERVICE
spec:
  ports:
    - port: 2181 # Zookeeper
      protocol: TCP
  selector:
    role: kafka-zk # Right now, we are running standalone kafka+zk combo image
  type: ClusterIP
