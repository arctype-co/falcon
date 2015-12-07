define(NAME, ifdef(`ZK_ID', SERVICE-ZK_ID, SERVICE))
kind: Service
apiVersion: v1
metadata:
  name: NAME
  labels:
    name: NAME
    ifdef(`ZK_ID', zk-id: "ZK_ID")
spec:
  ports:
    - port: 2181
      protocol: TCP
      name: zk-client
    - port: 2888 
      protocol: TCP
      name: zk-peer
    - port: 3888 
      protocol: TCP
      name: zk-elect
  selector:
    role: SERVICE
    ifdef(`ZK_ID', zk-id: "ZK_ID")
  type: ClusterIP
