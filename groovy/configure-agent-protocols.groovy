/*
Set agent protocol to JNLP4 only. This the most secure(TLS) and stable agent.
*/
import jenkins.model.*

def jenkins = Jenkins.getInstance()

println "Setting agent protocols"
Set<String> agentProtocolsList = ['JNLP4-connect', 'Ping']
jenkins.setAgentProtocols(agentProtocolsList)
jenkins.save()
