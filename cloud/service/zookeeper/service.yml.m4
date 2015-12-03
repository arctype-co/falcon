kind: Service
apiVersion: v1
metadata:
  name: SERVICE-ZK_ID
  labels:
    name: SERVICE-ZK_ID
spec:
  ports:
    - port: 2181 # Zookeeper
      protocol: TCP
  selector:
    role: SERVICE
    zk-id: ZK_ID
  type: ClusterIP
