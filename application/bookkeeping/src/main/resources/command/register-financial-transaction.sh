#!/usr/bin/env bash
set -euo pipefail

# register-financial-transaction.sh
# Registers a financial transaction.
# Usage: ./register-financial-transaction.sh
# Arguments: none.

main() {
  java -jar starter-all.jar "useCase" "RegisterFinancialTransaction"
}

main "$@"
