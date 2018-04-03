println 'Configuring Kubernetes plugin'

import org.csanchez.jenkins.plugins.kubernetes.*
import org.csanchez.jenkins.plugins.kubernetes.volumes.*
import jenkins.model.*

def jenkins = Jenkins.getInstance()

def kubernetes = new KubernetesCloud('kubernetes')
kubernetes.setSkipTlsVerify(true)
kubernetes.setCredentialsId('none')
kubernetes.setNamespace('jenkins')
kubernetes.setJenkinsUrl('http://jenkins-ui.jenkins.svc.cluster.local:8080')
kubernetes.setCredentialsId('none')
kubernetes.setContainerCapStr('10')
kubernetes.setRetentionTimeout(0)
kubernetes.setConnectTimeout(0)
kubernetes.setServerUrl('https://kubernetes.default.svc.cluster.local')
kubernetes.setJenkinsTunnel('jenkins-discovery.jenkins.svc.cluster.local:50000')
kubernetes.setMaxRequestsPerHostStr('32')

def jnlpContainer = new ContainerTemplate('jnlp')
jnlpContainer.setName('jnlp')
jnlpContainer.setImage('jenkins/jnlp-slave:3.19-1-alpine')
jnlpContainer.setCommand('')
jnlpContainer.setArgs('')
jnlpContainer.setTtyEnabled(true)

def dockerContainer = new ContainerTemplate('docker')
dockerContainer.setName('docker')
dockerContainer.setImage('docker:18.03.0-ce')
dockerContainer.setCommand('')
dockerContainer.setArgs('cat')
dockerContainer.setTtyEnabled(true)

def kubectlContainer = new ContainerTemplate('kubectl')
kubectlContainer.setName('kubectl')
kubectlContainer.setImage('lachlanevenson/k8s-kubectl:v1.10.0')
kubectlContainer.setCommand('cat')
kubectlContainer.setArgs('')
kubectlContainer.setTtyEnabled(true)

def jenkinsSlavePod = new PodTemplate()
jenkinsSlavePod.setName('jenkins-slave')
jenkinsSlavePod.setNamespace('jenkins')
jenkinsSlavePod.setLabel('jenkins-slave')
jenkinsSlavePod.setVolumes([
  new HostPathVolume('/var/run/docker.sock', '/var/run/docker.sock'),
  new SecretVolume('/home/jenkins/.ssh', 'jenkins-ssh-config'),
])
jenkinsSlavePod.setContainers([jnlpContainer, dockerContainer, kubectlContainer])

kubernetes.addTemplate(jenkinsSlavePod)

jenkins.clouds.replace(kubernetes)
jenkins.save()

println 'Kubernetes plugin configuration complete'

