define(`__SERVICE_TYPE__',
    ifelse(__ENVIRONMENT__, `production', `LoadBalancer',
           `ClusterIP'))
kind: Service
apiVersion: v1
metadata:
  name: app
  labels:
    name: app
spec:
  ports:
    - port: 3743
      targetPort: 3743
      protocol: TCP
  selector:
    role: app
  type: __SERVICE_TYPE__
