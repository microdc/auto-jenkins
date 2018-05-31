/*
Set up the ssh keys to be used with git
*/
import jenkins.model.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*

domain = Domain.global()
store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()


String keyfile = "/var/jenkins_home/.ssh/id_rsa"

privateKey = new BasicSSHUserPrivateKey(
CredentialsScope.GLOBAL,
'gitcreds',
'gitcreds',
new BasicSSHUserPrivateKey.FileOnMasterPrivateKeySource(keyfile),
"",
"",
)

store.addCredentials(domain, privateKey)

Jenkins.instance.save()
