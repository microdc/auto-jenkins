FROM equalexpertsmicrodc/ubuntu-testing-container
RUN mkdir /app
WORKDIR /app
COPY ./ /app/
RUN ./test.sh


FROM jenkins/jenkins:lts

# General Jenkins settings
COPY groovy/settings.groovy /usr/share/jenkins/ref/init.groovy.d/settings.groovy

