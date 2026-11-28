#!/usr/bin/env bash
set -euo pipefail

# solve-failures.sh
# Runs again the policies that failed.
# Usage: ./solve-failures.sh
# Arguments: none.

main() {
  java -jar starter-all.jar "useCase" "SolveFailures"
}

main "$@"
