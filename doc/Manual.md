# Falcon
Falcon builds and configures the various services for a Kubernetes cluster.

# Kubernetes 
Kubernetes is the cluster manager which orchestrates running docker images.

## Creating a new service

1. Write a Dockerfile template
`vim morph/myorg/myservice/Dockerfile.m4`
2. Build the container
`falcon container build myorg/myservice`
3. Define a replication controller
`vim morph/myorg/myservice/controller.yml.m4`
4. Deploy the controller
`falcon service create-rc myorg/myservice`
5. Define the network service
`vim morph/myorg/myservice/service.yml.m4`
6. Create the network service
`falcon service create myorg/myservice`

## Updating a controller
1. Deploy the new controller
`falcon service create-rc myorg/myservice`
2. Undelploy the old controller
`falcon service delete-rc myorg/myservice <controller-tag>`
