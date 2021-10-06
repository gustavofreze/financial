#!/bin/bash

psql <<EOF
\connect postgres;
EOF

CREATE="CREATE DATABASE financial_adm;"
SELECT="SELECT 1 FROM pg_database WHERE datname = 'financial_adm';"

printf "\n\nRunning DDL commands for the database financial_adm ... \n"

psql -U postgres -tc "${SELECT}" | grep -q 1 || psql -U postgres -c "${CREATE}"

printf "\n\nEnd of execution. \n"
