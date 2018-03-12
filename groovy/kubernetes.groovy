println 'Configuring Kubernetes plugin'

import org.csanchez.jenkins.plugins.kubernetes.*
import org.csanchez.jenkins.plugins.kubernetes.model.*;
import org.csanchez.jenkins.plugins.kubernetes.volumes.*;
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
jnlpContainer.setImage('jenkinsci/jnlp-slave')
jnlpContainer.setCommand('')
jnlpContainer.setArgs('')
jnlpContainer.setTtyEnabled(true)

def jenkinsSlavePod = new PodTemplate()
jenkinsSlavePod.setName('jenkins-slave')
jenkinsSlavePod.setNamespace('jenkins')
jenkinsSlavePod.setLabel('jenkins-slave')
jenkinsSlavePod.setContainers([jnlpContainer])

kubernetes.addTemplate(jenkinsSlavePod)

def KubectlContainer = new ContainerTemplate('kubectl')
KubectlContainer.setName('kubectl')
KubectlContainer.setImage('lachlanevenson/k8s-kubectl:latest')
KubectlContainer.setCommand('cat')
KubectlContainer.setArgs('')
KubectlContainer.setTtyEnabled(true)

def dockerUML = new ContainerTemplate('docker')
dockerUML.setName('docker')
dockerUML.setImage('microdc/docker-uml:latest')
dockerUML.setArgs('sleep inf')
dockerUML.setTtyEnabled(true)
dockerUML.setEnvVars([
  new KeyValueEnvVar('DISK', '90G'),
  new KeyValueEnvVar('MEM', '4G'),
  new KeyValueEnvVar('AWS_DEFAULT_REGION', 'eu-west-1'),
  new SecretEnvVar('AWS_ACCESS_KEY_ID', 'aws', 'AWS_ACCESS_KEY_ID'),
  new SecretEnvVar('AWS_SECRET_ACCESS_KEY', 'aws', 'AWS_SECRET_ACCESS_KEY'),
])

def buildPod = new PodTemplate()
buildPod.setName('buildpod')
buildPod.setNamespace('jenkins')
buildPod.setLabel('buildpod')
buildPod.setVolumes([
  new SecretVolume('/home/jenkins/.ssh', 'jenkins-ssh-keys'),
])
buildPod.setContainers([KubectlContainer, dockerUML])

kubernetes.addTemplate(buildPod)
kubernetes.addTemplate(jenkinsSlavePod)

jenkins.clouds.replace(kubernetes)
jenkins.save()

println 'Kubernetes plugin configuration complete'
