CREATE TABLE bookkeeping.transactions
(
    code              VARCHAR(36)       NOT NULL      CONSTRAINT transactions_pk PRIMARY KEY,
    payload           JSON              NOT NULL,
    revision          INTEGER           NOT NULL,
    sequence          INTEGER           NOT NULL,
    event_name        VARCHAR(255)      NOT NULL,
    occurred_on       TIMESTAMPTZ       NOT NULL,
    aggregate_id      VARCHAR(255)      NOT NULL,
    aggregate_type    VARCHAR(255)      NOT NULL
);

COMMENT ON COLUMN bookkeeping.transactions.code           IS 'Identificador do evento.';
COMMENT ON COLUMN bookkeeping.transactions.payload        IS 'Payload do evento como um json.';
COMMENT ON COLUMN bookkeeping.transactions.revision       IS 'Número positivo que indica a versão do payload do evento produzido.';
COMMENT ON COLUMN bookkeeping.transactions.sequence       IS 'Número positivo que indica a ordem de acontecimento dos eventos em um mesmo agregado.';
COMMENT ON COLUMN bookkeeping.transactions.event_name     IS 'Nome do evento.';
COMMENT ON COLUMN bookkeeping.transactions.occurred_on    IS 'Momento em que o evento ocorreu em UTC.';
COMMENT ON COLUMN bookkeeping.transactions.aggregate_id   IS 'Identificador da raiz de agregação.';
COMMENT ON COLUMN bookkeeping.transactions.aggregate_type IS 'Nome da raiz de agregação que produziu o evento.';

ALTER TABLE bookkeeping.transactions
    OWNER TO postgres;
