#!/bin/bash

set -e

main() {

  JENKINS_HOME="/var/jenkins_home"
  JOBDSL_DIR="${JENKINS_HOME}/jobdsl"
  REPOS_FILE="/usr/share/jenkins/data/repos.txt"
  TEMP_REPOS_DIR="/tmp/repos"

  mkdir -vp "${TEMP_REPOS_DIR}"

  # Get job dsl files from git repos
  if [ -f ${REPOS_FILE} ]; then
    echo "${REPOS_FILE} found!"
    mkdir -vp "${JOBDSL_DIR}"
    while IFS='' read -r repo || [[ -n "$repo" ]]; do

      # To speed up grabbing of jobdsl files in bitbucket (github doesnt support archive!!)
      if [[ "$repo" = *"bitbucket.org"* ]]; then
        repo=$(echo "${repo}" | tr ":" "/")
        git archive --remote="ssh://git@${repo}.git" HEAD "${repo##*/}.jobdsl" | tar xvf - -C "${JOBDSL_DIR}"
      else
        git clone -n --depth 1 "git@${repo}" "${TEMP_REPOS_DIR}/${repo#*/}"
        (cd "${TEMP_REPOS_DIR}/${repo#*/}" && git checkout HEAD "${repo#*/}.jobdsl")
        mv -v "${TEMP_REPOS_DIR}/${repo#*/}/${repo#*/}.jobdsl" "${JOBDSL_DIR}"
      fi
    done < "${REPOS_FILE}"
  else
    echo "${REPOS_FILE} does not exist, this should be mounted in"
  fi

  rm -rf "${TEMP_REPOS_DIR}" && echo "Cleaned TEMP_REPOS_DIR" &

}

main "$@"
