SELECT setval('account.balances_id_seq', (SELECT MAX(id) FROM account.balances));

GRANT USAGE ON ALL SEQUENCES IN SCHEMA account TO app;

ALTER DEFAULT PRIVILEGES IN SCHEMA account GRANT USAGE ON SEQUENCES TO app;
