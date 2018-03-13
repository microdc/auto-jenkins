# k8s-jenkins
Automated Jenkins build with sensible plugins and no setup wizard intended to run CI/CD on Kubernetes

Available on [Docker hub](https://hub.docker.com/r/microdc/k8s-jenkins/)

### Build
```
export VERSION='latest'
docker build --rm -t "microdc/k8s-jenkins:${VERSION}" .
# OR
./build.sh
```

### Local run example
```
docker run --rm -p 8080:8080 -p 50000:50000 microdc/k8s-jenkins:latest
```

## Kubernetes deployment
```
export JOB_DSL_URLS="microdc/k8s-jenkins" #this is currently bitbucket only use an empty string for github
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

## Generate and use an SSH key for bitbucket/github on k8s
```
export DATE=$(date '+%Y-%m-%d')
mkdir -vp "${HOME}/.ssh/jenkins"
ssh-keygen \
    -t rsa -b 4096 -C "Jenkins ${DATE}" \
    -f "${HOME}/.ssh/jenkins/id_rsa"
cat > "${HOME}/.ssh/jenkins/config" << EOF
Host *
  StrictHostKeyChecking no
EOF

kubectl create secret generic jenkins-ssh-keys --from-file="${HOME}/.ssh/jenkins/id_rsa" \
                                               --from-file="${HOME}/.ssh/jenkins/id_rsa".pub \
                                               --from-file="${HOME}/.ssh/jenkins/known_hosts" \
                                               --from-file="${HOME}/.ssh/jenkins/config" \
                                               -n jenkins
```
