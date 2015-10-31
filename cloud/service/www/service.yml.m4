define(`__SERVICE_TYPE__',
    ifelse(__ENVIRONMENT__, `production', `LoadBalancer',
           `ClusterIP'))
kind: Service
apiVersion: v1
metadata:
  name: __SERVICE_NAME__
  labels:
    name: __SERVICE_NAME__
spec:
  ports:
    - port: __SERVICE_PORT__
      targetPort: __SERVICE_PORT__
      protocol: TCP
  selector:
    role: __SERVICE_NAME__
  type: __SERVICE_TYPE__
