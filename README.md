# k8s-jenkins
Kubernetes deployment of Jenkins

Available on [Docker hub](https://hub.docker.com/r/equalexpertsmicrodc/k8s-jenkins/)

### Build
```
export VERSION='latest'
docker build --rm -t "equalexpertsmicrodc/jenkins:${VERSION}" .
```

### Local run example
```
docker run --rm -p 8080:8080 -p 50000:50000 equalexpertsmicrodc/jenkins:latest
```

## Kubernetes deployment
```
export APPNAME=jenkins
export ENVIRONMENT=dev
kubectl apply -f <(cat k8s.yaml.template | envsubst)
```
