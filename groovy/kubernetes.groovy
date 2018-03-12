println 'Configuring Kubernetes plugin'

import org.csanchez.jenkins.plugins.kubernetes.*
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

jenkins.clouds.replace(kubernetes)
jenkins.save()
