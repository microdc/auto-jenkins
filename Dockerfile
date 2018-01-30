FROM equalexpertsmicrodc/ubuntu-testing-container
RUN mkdir /app
WORKDIR /app
COPY ./ /app/
RUN ./test.sh


FROM jenkins/jenkins:lts
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
# Dont run the setup wizard
RUN echo '2.0' > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state
