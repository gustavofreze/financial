#!/bin/bash
set -euo pipefail

DB_NAME="financial_adm"
PSQL="psql -v ON_ERROR_STOP=1 -d postgres -U postgres -tAc"

printf "\n\nRunning DDL commands for the database %s ... \n" "$DB_NAME"

exists=$($PSQL "SELECT 1 FROM pg_database WHERE datname = '${DB_NAME}'" | tr -d '[:space:]' || true)

if [ "$exists" != "1" ]; then
  echo "Database ${DB_NAME} not found. Creating..."
  $PSQL "CREATE DATABASE \"${DB_NAME}\";"
else
  echo "Database ${DB_NAME} already exists. Skipping creation."
fi

printf "\n\nEnd of execution. \n"
