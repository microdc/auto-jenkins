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

def JnlpContainer = new ContainerTemplate('jnlp')
JnlpContainer.setName('jnlp')
JnlpContainer.setImage('jenkinsci/jnlp-slave')
JnlpContainer.setCommand('')
JnlpContainer.setArgs('')
JnlpContainer.setTtyEnabled(true)

def JenkinsSlavePod = new PodTemplate()
JenkinsSlavePod.setName('jenkins-slave')
JenkinsSlavePod.setNamespace('jenkins')
JenkinsSlavePod.setLabel('jenkins-slave')
JenkinsSlavePod.setContainers([JnlpContainer])

kubernetes.addTemplate(JenkinsSlavePod)

def KubectlContainer = new ContainerTemplate('kubectl')
KubectlContainer.setName('kubectl')
KubectlContainer.setImage('lachlanevenson/k8s-kubectl:latest')
KubectlContainer.setCommand('cat')
KubectlContainer.setArgs('')
KubectlContainer.setTtyEnabled(true)

def DockerUML = new ContainerTemplate('docker')
DockerUML.setName('docker')
DockerUML.setImage('microdc/docker-uml:latest')
DockerUML.setArgs('sleep inf')
DockerUML.setTtyEnabled(true)
DockerUML.setEnvVars([
  new KeyValueEnvVar('DISK', '90G'),
  new KeyValueEnvVar('MEM', '4G'),
  new KeyValueEnvVar('AWS_DEFAULT_REGION', 'eu-west-1'),
  new SecretEnvVar('AWS_ACCESS_KEY_ID', 'aws', 'AWS_ACCESS_KEY_ID'),
  new SecretEnvVar('AWS_SECRET_ACCESS_KEY', 'aws', 'AWS_SECRET_ACCESS_KEY'),
])

def buildpod = new PodTemplate()
buildpod.setName('buildpod')
buildpod.setNamespace('jenkins')
buildpod.setLabel('buildpod')
buildpod.setVolumes([
  new SecretVolume('/home/jenkins/.ssh', 'jenkins-ssh-keys'),
])
buildpod.setContainers([KubectlContainer, DockerUML])

kubernetes.addTemplate(buildpod)
kubernetes.addTemplate(JenkinsSlavePod)

jenkins.clouds.replace(kubernetes)
jenkins.save()

println 'Kubernetes plugin configuration complete'
