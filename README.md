# k8s-jenkins
Automated Jenkins build with sensible plugins and no setup wizard intended to run CI/CD on Kubernetes

Available on [Docker hub](https://hub.docker.com/r/equalexpertsmicrodc/k8s-jenkins/)

### Build
```
export VERSION='latest'
docker build --rm -t "equalexpertsmicrodc/k8s-jenkins:${VERSION}" .
# OR
./build.sh
```

### Local run example
```
docker run --rm -p 8080:8080 -p 50000:50000 equalexpertsmicrodc/k8s-jenkins:latest
```

## Kubernetes deployment
```
export APPNAME=jenkins
export ENVIRONMENT=dev
kubectl apply -f <(cat k8s.yaml.template | envsubst)
```

## Generate plugins.txt
From time to time we may need to generate a complete plugins list. This was generated from a container
following the original jenkins documentation [here](https://github.com/jenkinsci/docker/blob/master/README.md), like so:
```
JENKINS_HOST=admin:admin@localhost:8080
curl -sSL "http://$JENKINS_HOST/pluginManager/api/xml?depth=1&xpath=/*/*/shortName|/*/*/version&wrapper=plugins" | \
          perl -pe 's/.*?<shortName>([\w-]+).*?<version>([^<]+)()(<\/\w+>)+/\1 \2\n/g'|sed 's/ /:/' | \
          sort
```

