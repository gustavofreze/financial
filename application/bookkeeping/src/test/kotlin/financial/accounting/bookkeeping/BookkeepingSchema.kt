package financial.accounting.bookkeeping

import financial.shared.adapters.database.Database
import financial.shared.adapters.database.RowMapper

object BookkeepingSchema {

    val statements = arrayOf(
        "CREATE SCHEMA bookkeeping",
        """
        CREATE TABLE bookkeeping.transactions
        (
            code           VARCHAR(36)              NOT NULL,
            payload        TEXT                     NOT NULL,
            revision       INTEGER                  NOT NULL,
            sequence       INTEGER                  NOT NULL,
            event_name     VARCHAR(255)             NOT NULL,
            occurred_on    TIMESTAMP WITH TIME ZONE NOT NULL,
            aggregate_id   VARCHAR(255)             NOT NULL,
            aggregate_type VARCHAR(255)             NOT NULL,
            CONSTRAINT transactions_pk PRIMARY KEY (code)
        )
        """.trimIndent()
    )

    fun storedEvents(database: Database): List<StoredEvent> = database.fetchAll(
        sql = """
            SELECT payload, revision, sequence, event_name, occurred_on, aggregate_id, aggregate_type
            FROM bookkeeping.transactions
            ORDER BY sequence
        """.trimIndent(),
        mapper = RowMapper { row ->
            StoredEvent(
                payload = row.text(column = "payload"),
                revision = row.integer(column = "revision"),
                sequence = row.integer(column = "sequence"),
                eventName = row.text(column = "event_name"),
                occurredOn = row.dateTime(column = "occurred_on").toInstant(),
                aggregateId = row.text(column = "aggregate_id"),
                aggregateType = row.text(column = "aggregate_type")
            )
        },
        parameters = emptyMap()
    )
}
