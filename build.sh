#!/bin/bash

check_dependency() {
  local DEPENDANCY="${1}"
  command -v "${DEPENDANCY}" >/dev/null 2>&1 || err "${DEPENDANCY} is required but not installed.  Aborting."
}

err() {
  echo -e "[ERR] ${1}"
  exit 1
}

log() {
  echo -e "[LOG] ${1}"
}

main () {
  check_dependency docker

  export VERSION='latest'
  docker build --rm -t "equalexpertsmicrodc/k8s-jenkins:${VERSION}" .
  docker tag "equalexpertsmicrodc/k8s-jenkins:${VERSION}" "equalexpertsmicrodc/k8s-jenkins:latest"
}

main "$@"
