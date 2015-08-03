# Falcon
Falcon builds and configures the various services for the cluster.

## Publishing an app image

Tags should be in the form YYYY-mm-dd-x, where x is an incrementing letter.

Variables:
  - TAG is the tag for the docker image
  - APP_TAG is the git tag for the app.

```
make app-image-push TAG=2015-08-03-a APP_TAG=2015-08-03-a
```

# Kubernetes 
Kubernetes is the cluster manager which orchestrates running docker images.

## Connect to the master
```
ssh -L 8080:localhost:8080 -i ~/.ssh/google_compute_engine paul.etheride.com 
# Show cluster info
kubectl cluster-info
```

## Creating a new service

1. Create a template replication controller 
2. Create a service (exposes pods to the network)

## Deploying a new service

1. Deploy a replication controller
```
kubectl create -f my-replication-controller.yml
kubectl get rc
kubectl get pods
```

2. Define a service configuration (manages networking)
```
kubectl create -f my-service.yml
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

## Pushing a Docker image
docker tag etheride/image-name gcr.io/etheride-984/image-name:tag
gcloud docker push gcr.io/etheride-984/image-name:tag 
