apiVersion: v1
kind: Secret
metadata:
  name: SECRET
type: Opaque
data:
  chowdr-service.edn: CHOWDR_SERVICE_EDN_BASE64
  log4j.properties: LOG4J_PROPERTIES_BASE64
