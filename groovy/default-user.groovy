/*
Set the Jenkins admin and admin password from environment variables. (see Dockerfile)
*/


import jenkins.model.*
import hudson.security.*

def env = System.getenv()

def jenkins = Jenkins.getInstance()
jenkins.setSecurityRealm(new HudsonPrivateSecurityRealm(false))
jenkins.setAuthorizationStrategy(new GlobalMatrixAuthorizationStrategy())

def user = jenkins.getSecurityRealm().createAccount(env.JENKINS_USER, env.JENKINS_PASS)
user.save()
def reg_user = jenkins.getSecurityRealm().createAccount('registrator', 'sdfkjh73234kjhsdf123123asfdasd')
reg_user.save()

jenkins.getAuthorizationStrategy().add(Jenkins.ADMINISTER, env.JENKINS_USER)
def strategy = new GlobalMatrixAuthorizationStrategy()
    strategy.add(Jenkins.ADMINISTER, env.JENKINS_USER)
    strategy.add(Jenkins.ADMINISTER, 'registrator')

jenkins.setAuthorizationStrategy(strategy)


jenkins.save()
