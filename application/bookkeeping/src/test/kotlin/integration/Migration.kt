package integration

object Migration {

    val CREATE_SCHEMA_BOOKKEEPING = """
        CREATE SCHEMA bookkeeping;
        """.trimIndent()

    val CREATE_TABLE_TRANSACTIONS = """  
        CREATE TABLE bookkeeping.transactions
        (
            code                   VARCHAR(36)       NOT NULL        CONSTRAINT transactions_pk PRIMARY KEY,
            payload                JSON              NOT NULL,
            revision               INTEGER           NOT NULL,
            sequence               INTEGER           NOT NULL,
            event_name             VARCHAR(255)      NOT NULL,
            occurred_on            TIMESTAMP         NOT NULL,
            aggregate_id           VARCHAR(255)      NOT NULL,
            aggregate_type         VARCHAR(255)      NOT NULL
        )
        """.trimIndent()
}