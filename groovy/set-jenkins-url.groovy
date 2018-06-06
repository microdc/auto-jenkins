/*
Set the Jenkins URL from the environment
*/
import jenkins.model.JenkinsLocationConfiguration

externalurl = System.getenv("EXTERNAL_URL")

if (externalurl) {

  jlc = JenkinsLocationConfiguration.get()
  jlc.setUrl(externalurl)
  println(jlc.getUrl())
  jlc.save()

}
