println 'Running initseed.groovy'

import jenkins.model.Jenkins
import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.dsl.ScriptRequest
import javaposse.jobdsl.plugin.JenkinsJobManagement

def jenkinshome = Jenkins.getInstance().getRootDir().getPath()

def jobManagement = new JenkinsJobManagement(System.out, [:], new File('.'))

def filename = [ jenkinshome, 'jobdsl', 'seed.groovy' ].join(File.separator)

def scriptRequest = new ScriptRequest(new File(filename).text,
                                      new File('.').toURI().toURL(),
                                      true)

def dslScriptLoader =
  new DslScriptLoader(jobManagement).runScripts([scriptRequest])
dslScriptLoader.jobs.each { e -> println e.jobName }
dslScriptLoader.views.each { e -> println e.name }
