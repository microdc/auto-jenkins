#!/bin/bash

usage() {
  echo """
  USAGE
  --jobdslurls='url1;url2'  List urls of jobdsl files
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
        --jobdslurls)
            OPTION_JOB_DSL_URLS="${VALUE}"
            ;;
        *)
            JENKINS_PARAMS="${JENKINS_PARAMS} ${1}"
            ;;
    esac
    shift
  done

  for DSL_FILE_URL in $(echo "${OPTION_JOB_DSL_URLS}" | tr ";" "\n"); do
    JOBDSL_DIR="/var/jenkins_home/jobdsl"
    mkdir -vp "${JOBDSL_DIR}"
    echo "From ${DSL_FILE_URL} to ${JOBDSL_DIR}"
    wget -q "${DSL_FILE_URL}" -P "${JOBDSL_DIR}"
  done

  echo "START:"

  /bin/bash -c "/usr/local/bin/jenkins.sh ${JENKINS_PARAMS}"

}

main "$@"

