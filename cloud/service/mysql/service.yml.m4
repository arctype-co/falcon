kind: Service
apiVersion: v1
metadata:
  name: mysql
  labels:
    name: mysql
spec:
  ports:
    - port: 3306
      targetPort: 3306
      protocol: TCP
  selector:
    role: mysql
  type: ClusterIP
