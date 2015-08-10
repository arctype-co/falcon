define(`__SERVICE_TYPE__',
    ifelse(__ENVIRONMENT__, `prod', `LoadBalancer',
           `ClusterIP'))
kind: Service
apiVersion: v1
metadata:
  name: app-service-__ENVIRONMENT__
  labels:
    name: app-service-__ENVIRONMENT__
spec:
  ports:
    - port: 3743
      targetPort: 3743
      protocol: TCP
  selector:
    role: app
    environment: __ENVIRONMENT__
  type: __SERVICE_TYPE__
