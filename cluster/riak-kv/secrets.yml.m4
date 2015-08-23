apiVersion: v1
kind: Secret
metadata:
  name: riak-kv-config
type: Opaque
data:
  cluster-host: __CLUSTER_HOST_BASE64__
