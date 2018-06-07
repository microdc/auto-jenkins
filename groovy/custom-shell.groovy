import hudson.tasks.*
import jenkins.*
import jenkins.model.*

// Allows the user to set a different default shell for pipelines

jenkins_shell = System.getenv("JENKINS_SHELL")

if (jenkins_shell) {
  jenkins = Jenkins.getInstance()
  Shell.DescriptorImpl shell = jenkins.getExtensionList(Shell.DescriptorImpl.class).get(0)
  shell.setShell("/bin/bash")
  shell.save()
}
