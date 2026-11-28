#!/usr/bin/env bash
set -euo pipefail

# show-balance.sh
# Shows the balance of every account.
# Usage: ./show-balance.sh
# Arguments: none.

main() {
  java -jar starter-all.jar "useCase" "ShowBalance"
}

main "$@"
