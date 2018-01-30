FROM equalexpertsmicrodc/ubuntu-testing-container
RUN mkdir /app
WORKDIR /app
COPY ./ /app/
RUN ./test.sh


FROM jenkins/jenkins:lts
# General Jenkins settings
COPY groovy/settings.groovy /usr/share/jenkins/ref/init.groovy.d/settings.groovy
# Install plugins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt
# Dont run the setup wizard
RUN echo '2.0' > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state
