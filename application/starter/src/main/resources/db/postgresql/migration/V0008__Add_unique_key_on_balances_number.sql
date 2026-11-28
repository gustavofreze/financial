ALTER TABLE account.balances
    ADD CONSTRAINT unq_balances_number UNIQUE (number);
