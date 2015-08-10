define(`__SERVICE_TYPE__',
    ifelse(__ENVIRONMENT__, `prod', `LoadBalancer',
           `ClusterIP'))
kind: Service
apiVersion: v1
metadata:
  name: crossbar-service-__ENVIRONMENT__
  labels:
    name: crossbar-service-__ENVIRONMENT__
spec:
  ports:
    - port: 3745
      targetPort: 3745
      protocol: TCP
  selector:
    role: crossbar
    environment: __ENVIRONMENT__
  type: __SERVICE_TYPE__
