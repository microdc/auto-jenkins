# auto-jenkins
Automated Jenkins build with sensible plugins and no setup wizard intended to run CI/CD on Kubernetes

Available on [Docker hub](https://hub.docker.com/r/microdc/auto-jenkins/)

  * [auto-jenkins](#auto-jenkins)
    * [Build](#build)
    * [Local run example](#local-run-example)
    * [Generate plugins.txt](#generate-pluginstxt)
    * [Deploy on Kubernetes](#deploy-on-kubernetes)
    * [Testing using Minikube](#testing-using-minikube)
    * [Accessing Jenkins externally](#accessing-jenkins-externally)

## Pull
The image is built at Dockerhub: https://hub.docker.com/r/microdc/auto-jenkins/
This is a public repo so can be launched as microdc/auto-jenkins:<tag>, e.g.
microdc/auto-jenkins:2.6.12

## Build

```
export VERSION='local'
docker build --rm -t "microdc/auto-jenkins:${VERSION}" .
# OR
./build.sh

```

## Local run example
This is only useful for testing changes to jenkins config. If you want to test kubernetes specific functionality follow the procedure below.  See step 3 below to generate the config files.
```
docker run --rm -p 8080:8080 -p 50000:50000 \
                -v "${PWD}/repos.txt":/usr/share/jenkins/data/repos.txt \
                -v "${PWD}/ssh_config/config":/var/jenkins_home/.ssh/config \
                -v "${HOME}/.ssh/id_rsa":/var/jenkins_home/.ssh/id_rsa \
                microdc/auto-jenkins:local
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

## Deploy on Kubernetes
1. Run kubectl to create the deployment and Jenkins Namespace. The containers wont run until the config is created below.
```
kubectl apply -f https://raw.githubusercontent.com/microdc/auto-jenkins/master/k8s.yaml
```
2. Create a config map for the git repos you will use (example file repos.txt)
```
kubectl create configmap jenkins-git-repos -n jenkins --from-file=repos.txt
```
3. Create Jenkins ssh config and keys secrets in Kubernetes
SSH keys are for git repos. The public keys generated here will need to be uploaded to your git provider.
```
export DATE=$(date '+%Y-%m-%d')
mkdir -vp "${HOME}/.ssh/jenkins"
ssh-keygen \
    -t rsa -b 4096 -C "Jenkins ${DATE}" \
    -f "${HOME}/.ssh/jenkins/id_rsa"
cat > "${HOME}/.ssh/jenkins/config" << EOF
Host *
  StrictHostKeyChecking no
  UserKnownHostsFile /dev/null
EOF
```
4. Add the ssh configuration to Kubernetes
```
kubectl create secret generic jenkins-ssh-config -n jenkins \
                                                 --from-file="${HOME}/.ssh/jenkins/config" \
                                                 --from-file="${HOME}/.ssh/jenkins/id_rsa" \
                                                 --from-file="${HOME}/.ssh/jenkins/id_rsa.pub"
```
5. Set Jenkins password
```
kubectl create secret generic jenkins-admin-creds -n jenkins --from-literal=username=admin --from-literal=password=admin
```

6. Add additional secrets to jenkins environment variables (key=value)
```
kubectl create secret generic jenkins-secret-env-vars -n jenkins --from-file="secrets.properties"
```

7. Access using the jenkins UI
`kubectl port-forward service/jenkins 8080 -n jenkins`

## Testing using Minikube
1. [Install Minikube](https://kubernetes.io/docs/tasks/tools/install-minikube/)
2. Start Minikube with a decent amount of memory
```
minikube start --memory 8192
```
3. Point your docker env to the MiniKube docker instance
```
eval $(minikube docker-env)
```
4. Build your image for minikube to use
```
docker build --rm -t "microdc/auto-jenkins:local" .
```
5. Follow the instruction for 'Deploy on Kubernetes'

**_Minikube tip_**
_To upload a container you've already built on your laptop to your minikube deploy run the following. This is also means your builds persist across destruction of your minikube cluster_
`docker save microdc/auto-jenkins:local | (eval $(minikube docker-env) && docker load)`

## Accessing Jenkins externally
If you need to access Jenkins externally I recommend you use an [oauth2 proxy](https://github.com/microdc/oauth2-proxy).
It's more secure than Jenkins and allows you to utilise the user management features of a 3rd party service like google or github.
Once you've chosen how Jenkins will be exposed you will need to set up a service or ingress to allow access. Jenkins should be configured with an external url so that links work etc.
This is set when Jenkins starts by a groovy script that looks for the EXTERNAL_URL variable below. We also need to set the hudson.TcpSlaveAgentListener.hostName option to the name jenkins will use internally.
If you are following the k8s.yaml config example this will be jenkins as below. If this is not set jenkins wont accept connections on anything other than what you set EXTERNAL_URL to.
```
    env:
      - name: EXTERNAL_URL
        value: https://jenkins.microdc.example/
      - name: JAVA_OPTS
        value: '-Xmx1400m -Dhudson.TcpSlaveAgentListener.hostName=jenkins'
```
