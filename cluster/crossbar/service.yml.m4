define(`__SERVICE_TYPE__',
    ifelse(__ENVIRONMENT__, `prod', `LoadBalancer',
           `ClusterIP'))
kind: Service
apiVersion: v1
metadata:
  name: crossbar
  labels:
    name: crossbar
spec:
  ports:
    - port: 3745
      targetPort: 3745
      protocol: TCP
  selector:
    role: crossbar
  type: __SERVICE_TYPE__
