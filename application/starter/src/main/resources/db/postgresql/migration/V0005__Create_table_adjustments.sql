CREATE TABLE account.adjustments
(
    transaction_id    VARCHAR(36)       NOT NULL       CONSTRAINT adjustments_pk PRIMARY KEY,
    adjusted_at       TIMESTAMPTZ       NOT NULL
);

COMMENT ON COLUMN account.adjustments.transaction_id IS '[NONE] Identificador da transação cujo ajuste de saldo foi aplicado.';
COMMENT ON COLUMN account.adjustments.adjusted_at    IS '[NONE] Momento em que o ajuste de saldo foi aplicado, em UTC.';

ALTER TABLE account.adjustments
    OWNER TO postgres;
