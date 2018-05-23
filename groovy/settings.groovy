/*
Set various Jenkins options. See comments below.
*/

import jenkins.model.*
import hudson.security.csrf.DefaultCrumbIssuer
import jenkins.security.s2m.AdminWhitelistRule

// Set the number of Jenkins executors
Jenkins.instance.setNumExecutors(0)

// Disable Jenkins CLI over remote
Jenkins.instance.getDescriptor("jenkins.CLI").get().setEnabled(false)

// Enable CSRF Protection
Jenkins.instance.setCrumbIssuer(new DefaultCrumbIssuer(true))

// Enable agent to master security subsystem
Jenkins.instance.getInjector().getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false)

Jenkins.instance.save()
