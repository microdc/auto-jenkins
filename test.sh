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

test_shell_files () {
  log "Testing shell files"
  grep -Rl '^#!/bin/bash' ./* | xargs shellcheck -e SC2044,SC1091 || err "Shellcheck errors"
}

test_yaml_files () {
  log "Testing yaml files"
  yamllint .
  #for file in $(find . -name '*.y*ml'); do
  #  python3 -c 'import yaml,sys;yaml.safe_load(sys.stdin)' < "${file}" || err "${file} has syntax errors"
  #done
}

main () {
  check_dependency shellcheck
  check_dependency python3
  check_dependency yamllint
  case $1 in
    shell)
        test_shell_files
        ;;
    yaml)
        test_yaml_files
        ;;
    *)
        test_shell_files
        test_yaml_files
        ;;
    esac
}

main "$@"
