kind: Service
apiVersion: v1
metadata:
  name: riak-kv
  labels:
    name: riak-kv
spec:
  ports:
    - port: 8097
      targetPort: 8097
      protocol: TCP
      name: protobuf-client
    - port: 8098
      targetPort: 8098
      protocol: TCP
      name: http-client
  selector:
    role: riak-kv
  type: ClusterIP
