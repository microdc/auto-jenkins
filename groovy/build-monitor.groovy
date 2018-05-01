/*
Create build monitor consisting of all jobs that end in -build
*/

import jenkins.model.*
import com.smartcodeltd.jenkinsci.plugins.buildmonitor.*

def name = 'build_monitor'
def title = 'Build Monitor'

def buildMonitorView = new BuildMonitorView(name, title)

buildMonitorView.setIncludeRegex('.+-build')

def jenkins = Jenkins.getInstance()

def view = jenkins.getView(name)

if(!view) {
  jenkins.addView(buildMonitorView)
  println("Added view ${title}")
} else {
  println("View already exists")
}

jenkins.save()
