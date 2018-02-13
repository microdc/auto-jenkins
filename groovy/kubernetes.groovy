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

def container = new ContainerTemplate('jnlp')
container.setName('jnlp')
container.setImage('jenkinsci/jnlp-slave')
container.setCommand('')
container.setArgs('')
container.setTtyEnabled(true)

def pod = new PodTemplate()
pod.setName('jenkins-slave')
pod.setNamespace('jenkins')
pod.setLabel('jenkins-slave')
pod.setContainers([container])

kubernetes.addTemplate(pod)

jenkins.clouds.replace(kubernetes)
jenkins.save()
