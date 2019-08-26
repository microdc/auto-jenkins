/*
Setting Global properties (Environment variables)
*/

import hudson.slaves.EnvironmentVariablesNodeProperty
import jenkins.model.Jenkins

instance = Jenkins.getInstance()
globalNodeProperties = instance.getGlobalNodeProperties()
envVarsNodePropertyList = globalNodeProperties.getAll(EnvironmentVariablesNodeProperty.class)

newEnvVarsNodeProperty = null
envVars = null

if ( envVarsNodePropertyList == null || envVarsNodePropertyList.size() == 0 ) {
  newEnvVarsNodeProperty = new EnvironmentVariablesNodeProperty();
  globalNodeProperties.add(newEnvVarsNodeProperty)
  envVars = newEnvVarsNodeProperty.getEnvVars()
} else {
  envVars = envVarsNodePropertyList.get(0).getEnvVars()
}
println "Setting Global properties (Environment variables)"

// Load secrets if secrets.properties is present
def secretsFile = new File( '/usr/share/jenkins/secrets/secrets.properties' )
if( secretsFile.exists() ) {
  Properties properties = new Properties()
  properties.load(secretsFile.newDataInputStream())
  properties.each{ k, v -> envVars.put(k, v) }
}

instance.save()
