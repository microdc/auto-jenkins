FROM equalexpertsmicrodc/ubuntu-testing-container
RUN mkdir /app
WORKDIR /app
COPY ./ /app/
RUN ./test.sh

# Skip initial setup
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

FROM jenkins/jenkins:2.104-alpine
# General Jenkins settings
COPY groovy/settings.groovy /usr/share/jenkins/ref/init.groovy.d/settings.groovy

# Set the default admin user and password
ENV JENKINS_USER admin
ENV JENKINS_PASS admin
COPY groovy/default-user.groovy /usr/share/jenkins/ref/init.groovy.d/

# Set log level
COPY log.properties /var/jenkins_home/log.properties

# Install plugins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

RUN echo 2 > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state
RUN echo 2 > /usr/share/jenkins/ref/jenkins.install.InstallUtil.lastExecVersion
