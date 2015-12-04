apiVersion: v1
kind: Secret
metadata:
  name: SECRET
type: Opaque
data:
  log4j.properties: LOG4J_PROPERTIES_BASE64
