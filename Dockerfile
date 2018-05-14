FROM microdc/ubuntu-testing-container:v0.0.1
RUN mkdir /app
WORKDIR /app
COPY ./ /app/
RUN ./test.sh

# Skip initial setup
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

FROM jenkins/jenkins:2.121-alpine

# Set the default admin user and password
ENV JENKINS_USER admin
ENV JENKINS_PASS admin

# Set log level
COPY log.properties /var/jenkins_home/log.properties

# Install plugins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

RUN echo 2 > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state
RUN echo 2 > /usr/share/jenkins/ref/jenkins.install.InstallUtil.lastExecVersion

# Copy Jenkins groovy configuration scripts
COPY groovy /usr/share/jenkins/ref/init.groovy.d/

# Copy seed job to bootstrap all jobdsl jobs
COPY seed.jobdsl /usr/share/jenkins/ref/jobdsl/seed.jobdsl

# Custom entry point to allow for download of jobdsl files from repos
COPY entrypoint.sh /usr/local/bin/entrypoint.sh

# Install docker
USER root
#RUN [ ! -e /etc/nsswitch.conf ] && echo 'hosts: files dns' > /etc/nsswitch.conf
ENV DOCKER_CHANNEL stable
ENV DOCKER_VERSION 18.03.1-ce
RUN echo 'docker:x:1001:jenkins' >> /etc/group
RUN set -ex; \
  dockerArch="$(apk --print-arch)"; \
  if ! curl -fL -o docker.tgz "https://download.docker.com/linux/static/${DOCKER_CHANNEL}/${dockerArch}/docker-${DOCKER_VERSION}.tgz"; then \
    echo >&2 "error: failed to download 'docker-${DOCKER_VERSION}' from '${DOCKER_CHANNEL}' for '${dockerArch}'"; \
    exit 1; \
  fi; \
  tar --extract --file docker.tgz --strip-components 1 --directory /usr/local/bin/ ; \
  rm docker.tgz; \
  dockerd -v; \
  docker -v

COPY modprobe.sh /usr/local/bin/modprobe


USER jenkins


ENTRYPOINT ["/sbin/tini", "--", "/usr/local/bin/entrypoint.sh"]
