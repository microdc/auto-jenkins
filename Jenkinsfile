def appName = 'k8s-jenkins'
def gitProvider = 'github.com'
def appRepo = "microdc/${appName}"
def label = "${UUID.randomUUID().toString()}"

podTemplate(label: label, inheritFrom: 'jenkins-slave') {

  node(label) {

    stage('Checkout repo') {
      git url: "git@${gitProvider}:${appRepo}.git"
    }

    stage('Build') {
      container('docker') {
        sh "/bin/sh build.sh"
      }
    }
  }
}

