define(`__SERVICE_TYPE__',
    ifelse(__ENVIRONMENT__, `production', `LoadBalancer',
           `ClusterIP'))
kind: Service
apiVersion: v1
metadata:
  name: www
  labels:
    name: www
spec:
  ports:
    - port: 80
      targetPort: 4000
      protocol: TCP
  selector:
    role: www
  type: __SERVICE_TYPE__
