/*
Setting Global properties (Environment variables)
*/
@Grab('org.yaml:snakeyaml:1.17')
import hudson.slaves.EnvironmentVariablesNodeProperty
import jenkins.model.Jenkins
import org.yaml.snakeyaml.Yaml

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
envVars.put("AWS_DEFAULT_REGION", "eu-west-1")

// Load secrets if secrets.yaml is present
def secretsfile = new File( '/usr/share/jenkins/secrets/secrets.yaml' )
if( secretsfile.exists() ) {
  Yaml parser = new Yaml()
  HashMap secrets = parser.load(secretsfile.text)
  secrets.each{ k, v -> envVars.put(k, v) }
}

instance.save()
