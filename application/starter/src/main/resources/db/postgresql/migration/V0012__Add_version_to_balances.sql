ALTER TABLE account.balances
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

COMMENT ON COLUMN account.balances.version IS '[NONE] The version of the balance that each adjustment increments by one, so a write based on a stale read changes nothing (e.g., 3).';
