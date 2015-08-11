apiVersion: v1
kind: Secret
metadata:
  name: app-config
type: Opaque
data:
  etheride.edn: __ETHERIDE_EDN_BASE64__
