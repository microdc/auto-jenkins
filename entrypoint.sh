#!/bin/bash

usage() {
  echo """
  USAGE
  --jobdslrepos='url1;url2'  List urls of jobdsl files
  """
  exit 0
}

main() {

  while [ "$1" != "" ]; do
    PARAM=$(echo "$1" | awk -F= '{print $1}')
    VALUE=$(echo "$1" | awk -F= '{print $2}')
    case $PARAM in
        --help)
            usage
            ;;
        --jobdslrepos)
            OPTION_JOB_DSL_REPO="${VALUE}"
            ;;
        *)
            JENKINS_PARAMS="${JENKINS_PARAMS} ${1}"
            ;;
    esac
    shift
  done

  JENKINS_HOME="/var/jenkins_home"
  JOBDSL_DIR="${JENKINS_HOME}/jobdsl"

  # Get gob dsl files from urls
  for DSL_FILE_REPO in $(echo "${OPTION_JOB_DSL_REPO}" | tr ";" "\n"); do
    mkdir -vp "${JOBDSL_DIR}"
    git clone --depth 1 "git@bitbucket.org:${DSL_FILE_REPO}" "/tmp/${DSL_FILE_REPO#*/}"
    mv -v "/tmp/${DSL_FILE_REPO#*/}/${DSL_FILE_REPO#*/}.jobdsl" "${JOBDSL_DIR}"
    rm -rfv "/tmp/${DSL_FILE_REPO#*/}"
  done

  echo "START JENKINS:"

  /bin/bash -c "/usr/local/bin/jenkins.sh ${JENKINS_PARAMS}"

}

main "$@"

