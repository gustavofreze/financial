CREATE TABLE account.troubleshoot
(
    id             VARCHAR(36)         NOT NULL     CONSTRAINT troubleshoot_pk PRIMARY KEY,
    error          TEXT                NOT NULL,
    resent         SMALLINT            NOT NULL DEFAULT 0,
    payload        JSON                NOT NULL,
    created_at     TIMESTAMPTZ         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMPTZ,
    event_name     VARCHAR(255)        NOT NULL,
    policy_name    VARCHAR(255)        NOT NULL
);

COMMENT ON COLUMN account.troubleshoot.id          IS 'Identificador do erro de processamento.';
COMMENT ON COLUMN account.troubleshoot.error       IS 'Descrição do erro ocorrido durante o processamento.';
COMMENT ON COLUMN account.troubleshoot.resent      IS 'Indica se o registro foi reenviado.';
COMMENT ON COLUMN account.troubleshoot.payload     IS 'Payload do evento.';
COMMENT ON COLUMN account.troubleshoot.created_at  IS 'Data em que o registro foi inserido.';
COMMENT ON COLUMN account.troubleshoot.updated_at  IS 'Data em que o registro foi atualizado.';
COMMENT ON COLUMN account.troubleshoot.event_name  IS 'Nome do evento.';
COMMENT ON COLUMN account.troubleshoot.policy_name IS 'Nome da política que estava sendo executada.';

ALTER TABLE account.troubleshoot
    OWNER TO postgres;
