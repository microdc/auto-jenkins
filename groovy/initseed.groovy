/*
Create the initial seed job
*/

println 'Running initseed.groovy'

import hudson.model.*
import hudson.AbortException
import hudson.console.HyperlinkNote
import java.util.concurrent.CancellationException
import jenkins.model.Jenkins
import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.dsl.ScriptRequest
import javaposse.jobdsl.plugin.JenkinsJobManagement

// Set the number of Jenkins executors so master can run seed job
Jenkins.instance.setNumExecutors(1)
Jenkins.instance.save()

def jenkinshome = Jenkins.getInstance().getRootDir().getPath()

def jobManagement = new JenkinsJobManagement(System.out, [:], new File('.'))

def filename = [ jenkinshome, 'jobdsl', 'seed.jobdsl' ].join(File.separator)

def scriptRequest = new ScriptRequest(new File(filename).text,
                                      new File('.').toURI().toURL(),
                                      true)

def dslScriptLoader =
  new DslScriptLoader(jobManagement).runScripts([scriptRequest])
dslScriptLoader.jobs.each { e -> println e.jobName }
dslScriptLoader.views.each { e -> println e.name }

//run the seed job
def job = Hudson.instance.getJob('seed')
def anotherBuild
try {
    def future = job.scheduleBuild2(0)
    println "Waiting for the completion of " + HyperlinkNote.encodeTo('/' + job.url, job.fullDisplayName)
    anotherBuild = future.get()
} catch (CancellationException x) {
    throw new AbortException("${job.fullDisplayName} aborted.")
}

Jenkins.instance.save()
