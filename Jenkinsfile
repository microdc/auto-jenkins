pipeline {
    agent any
    stages {
        stage('deploy') {
            steps {
                build()
            }
        }
    }
}

def build() {
    sh """
    ./build.sh
    """
}
