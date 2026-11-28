CREATE ROLE debezium WITH LOGIN REPLICATION PASSWORD 'Qz7&dbzLc4Tm';
COMMENT ON ROLE debezium IS 'User the Debezium connector reads the bookkeeping transactions with.';

GRANT CONNECT ON DATABASE financial_adm TO debezium;
GRANT USAGE ON SCHEMA bookkeeping TO debezium;
GRANT SELECT ON bookkeeping.transactions TO debezium;

DROP PUBLICATION IF EXISTS dbz_publication;
CREATE PUBLICATION dbz_publication FOR TABLE bookkeeping.transactions;
