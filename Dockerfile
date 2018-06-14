# lint the yaml files and check the shell scripts
FROM microdc/ubuntu-testing-container:v0.0.1
RUN mkdir /app
WORKDIR /app
COPY ./ /app/
RUN ./test.sh

# Skip initial setup
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

FROM jenkins/jenkins:2.127-alpine

USER jenkins

# Set the default admin user and password
ENV JENKINS_USER admin
ENV JENKINS_PASS admin

# Set log level
COPY log.properties /var/jenkins_home/log.properties

# Install plugins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

RUN echo 2 > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state && \
    echo 2 > /usr/share/jenkins/ref/jenkins.install.InstallUtil.lastExecVersion

# Copy Jenkins groovy configuration scripts
COPY groovy /usr/share/jenkins/ref/init.groovy.d/

# Copy seed job to bootstrap all jobdsl jobs
COPY seed.jobdsl /usr/share/jenkins/ref/jobdsl/seed.jobdsl

# Custom entry point to allow for download of jobdsl files from repos
COPY entrypoint.sh /usr/local/bin/entrypoint.sh

USER root
RUN apk --no-cache add shadow su-exec docker groff python py-pip gettext && \
    pip install awscli==1.15.21 s3cmd==2.0.1
RUN [ ! -e /etc/nsswitch.conf ] && echo 'hosts: files dns' > /etc/nsswitch.conf

COPY modprobe.sh /usr/local/bin/modprobe

#Install kubectl
RUN curl -L -o /usr/bin/kubectl https://storage.googleapis.com/kubernetes-release/release/v1.10.2/bin/linux/amd64/kubectl && chmod +x /usr/bin/kubectl


ENTRYPOINT ["/sbin/tini", "--", "/usr/local/bin/entrypoint.sh"]
