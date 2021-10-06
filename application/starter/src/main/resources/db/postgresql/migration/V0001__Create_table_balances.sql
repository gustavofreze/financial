CREATE TABLE account.balances
(
    id            SERIAL             NOT NULL       CONSTRAINT balance_pk PRIMARY KEY,
    number        VARCHAR(255)       NOT NULL,
    balance       NUMERIC(15, 2)     NOT NULL,
    currency      VARCHAR(3)         NOT NULL,
    updated_at    TIMESTAMPTZ        NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON COLUMN account.balances.id         IS 'Identificador da tabela.';
COMMENT ON COLUMN account.balances.number     IS 'Número da conta.';
COMMENT ON COLUMN account.balances.balance    IS 'Saldo da conta.';
COMMENT ON COLUMN account.balances.currency   IS 'Moeda do saldo.';
COMMENT ON COLUMN account.balances.updated_at IS 'Data em que o registro foi atualizado.';

ALTER TABLE account.balances
    OWNER TO postgres;
