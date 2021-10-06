package integration

object Migration {

    val CREATE_SCHEMA_ACCOUNT = """
        CREATE SCHEMA account;
        """.trimIndent()

    val CREATE_TABLE_BALANCES = """
        CREATE TABLE account.balances
        (
            id            SERIAL             NOT NULL       CONSTRAINT balance_pk PRIMARY KEY,
            number        VARCHAR(255)       NOT NULL,
            balance       NUMERIC(15, 2)     NOT NULL,
            currency      VARCHAR(3)         NOT NULL,
            updated_at    TIMESTAMP          NOT NULL
        )       
        """.trimIndent()

    val CREATE_TABLE_TROUBLESHOOT = """
        CREATE TABLE account.troubleshoot
        (
            id             VARCHAR(36)         NOT NULL     CONSTRAINT troubleshoot_pk PRIMARY KEY,
            error          TEXT                NOT NULL,
            resent         SMALLINT            NOT NULL DEFAULT 0,
            payload        TEXT                NOT NULL,
            created_at     TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
            updated_at     TIMESTAMP,
            event_name     VARCHAR(255)        NOT NULL,
            policy_name    VARCHAR(255)        NOT NULL
        )       
        """.trimIndent()

    val INSERT_INTO_BALANCES = """
        INSERT INTO account.balances (id, number, balance, currency, updated_at)
        VALUES (1, '2.1.1.001', 100.00, 'BRL', NOW()),
               (2, '2.2.1.001', 100.00, 'BRL', NOW());       
        """.trimIndent()
}