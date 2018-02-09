#!/bin/bash

usage() {
  echo """
  USAGE
  --jobdslurls='url1;url2'  List urls of jobdsl files
  --sshprivatekey='privatekey'  Generic private key for version control auth
  --sshpublickey='publickey'    The public key for the private key above, mainly so its not lost
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
        --sshprivatekey)
            OPTION_SSH_PRIAVTE_KEY="${VALUE}"
            ;;
        --sshpublickey)
            OPTION_SSH_PUBLIC_KEY="${VALUE}"
            ;;
        *)
            JENKINS_PARAMS="${JENKINS_PARAMS} ${1}"
            ;;
    esac
    shift
  done

  JENKINS_HOME="/var/jenkins_home"

  # Get gob dsl files from urls
  for DSL_FILE_URL in $(echo "${OPTION_JOB_DSL_URLS}" | tr ";" "\n"); do
    JOBDSL_DIR="${JENKINS_HOME}/jobdsl"
    mkdir -vp "${JOBDSL_DIR}"
    echo "From ${DSL_FILE_URL} to ${JOBDSL_DIR}"
    wget -q "${DSL_FILE_URL}" -P "${JOBDSL_DIR}"
  done

  # Store ssh key in jenkins home
  JENKINS_SSH_DIR="${JENKINS_HOME}/.ssh"
  mkdir -vp "${JENKINS_SSH_DIR}"
  echo "${OPTION_SSH_PRIAVTE_KEY}" > "${JENKINS_SSH_DIR}/id_rsa"
  echo "${OPTION_SSH_PUBLIC_KEY}" >"${JENKINS_SSH_DIR}/id_rsa.pub"

  echo "START JENKINS:"

  /bin/bash -c "/usr/local/bin/jenkins.sh ${JENKINS_PARAMS}"

}

main "$@"

