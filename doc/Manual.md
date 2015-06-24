# Kubernetes Manual

## Connect to the master
```
MASTER_HOST=23.251.146.67
ssh -L 8080:localhost:8080 -i .ssh/google_compute_engine $MASTER_HOST 
kubectl cluster-info
```

## Deploying a new service

1. Deploy a replication controller
```
kubectl create -f my-replication-controller.json
kubectl get rc
kubectl get pods
```

2. Define a service configuration (manages networking)
```
kubectl create -f my-service.json
kubectl get services
```

3. Open firewall port to cluster nodes (external services only)

```
gcloud compute firewall-rules create --allow=tcp:$PORT --target-tags=k8s-cluster-1-node my-service-$PORT
```

## Creating a disk
```
gcloud compute disks create --size=500GB --zone=us-central1-f my-data-disk
```
