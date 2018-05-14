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

  JENKINS_HOME="/var/jenkins_home"
  JOBDSL_DIR="${JENKINS_HOME}/jobdsl"
  REPOS_FILE="/usr/share/jenkins/data/repos.txt"

  # Get gob dsl files from git repos
  if [ -f ${REPOS_FILE} ]; then
    echo "${REPOS_FILE} found!"
    mkdir -vp "${JOBDSL_DIR}"
    while IFS='' read -r repo || [[ -n "$repo" ]]; do
      git clone --depth 1 "git@${repo}" "/tmp/${repo#*/}"
      mv -v "/tmp/${repo#*/}/${repo#*/}.jobdsl" "${JOBDSL_DIR}"
      rm -rfv "/tmp/${repo#*/}"
    done < "${REPOS_FILE}"
  else
    echo "${REPOS_FILE} does not exist, this should be mounted in"
  fi

  echo "START JENKINS:"

  /bin/bash -c "/usr/local/bin/jenkins.sh ${JENKINS_PARAMS}"

}

main "$@"

