#!/bin/bash

set -e

usage() {
  echo """
  USAGE
  entrypoint.sh JENKINS_PARAMS
                --help #this page
  """
  exit 0
}

main() {

  while [ "$1" != "" ]; do
    local PARAM=$1
    case ${PARAM} in
        --help)
            usage
            ;;
        *)
            JENKINS_PARAMS="${JENKINS_PARAMS} ${PARAM}"
            ;;
    esac
    shift
  done

  echo "$(date) START JENKINS SETUP"

  # Detect host docker socket perms
  DOCKER_SOCKET=/var/run/docker.sock
  DOCKER_GROUP=docker

  if [ -S ${DOCKER_SOCKET} ]; then
      DOCKER_GID="$(stat -c '%g' ${DOCKER_SOCKET})"
      OLD_GROUP="$(stat -c '%G' ${DOCKER_SOCKET})"

      # Ensure there is not an old `docker` group (DOCKER_GROUP), or an old group using
      # docker's desired GID (OLD_GROUP). `delgroup` only takes a group name so we
      # cannot delete by GID.
      /usr/sbin/delgroup "${DOCKER_GROUP}"
      [[ "${OLD_GROUP}" != "UNKNOWN" ]] && /usr/sbin/delgroup "${OLD_GROUP}"

      /usr/sbin/addgroup -S -g "${DOCKER_GID}" "${DOCKER_GROUP}"
      /usr/sbin/addgroup "jenkins" "${DOCKER_GROUP}"
  fi

  echo "$(date) START JENKINS:"

  /bin/bash -c "su-exec jenkins /usr/local/bin/jenkins.sh ${JENKINS_PARAMS}"

}

main "$@"
