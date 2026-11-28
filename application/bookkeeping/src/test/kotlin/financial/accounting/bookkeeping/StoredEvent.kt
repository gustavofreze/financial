package financial.accounting.bookkeeping

import java.time.Instant

data class StoredEvent(
    val payload: String,
    val revision: Int,
    val sequence: Int,
    val eventName: String,
    val occurredOn: Instant,
    val aggregateId: String,
    val aggregateType: String
)
