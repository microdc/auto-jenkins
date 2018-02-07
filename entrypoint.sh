#!/bin/bash

set -e

main() {

  while [ "$1" != "" ]; do
    PARAM=$(echo "$1" | awk -F= '{print $1}')
    VALUE=$(echo "$1" | awk -F= '{print $2}')
    case $PARAM in
        --gitrepos)
            OPTION_GITREPOS="${VALUE}"
            ;;
        *)
            JENKINS_PARAMS="${JENKINS_PARAMS} ${1}"
            ;;
    esac
    shift
  done

  for REPO in $(echo "${OPTION_GITREPOS}" | tr ";" "\n"); do
    REPO_NAME=${REPO##*/}
    curl -s "${REPO}/master/${REPO_NAME}.jobdsl" -o "/usr/share/jenkins/ref/jobdsl/${REPO_NAME}.jobdsl"
  done

  /bin/bash -c "/usr/local/bin/jenkins.sh ${JENKINS_PARAMS}"

}

main "$@"

