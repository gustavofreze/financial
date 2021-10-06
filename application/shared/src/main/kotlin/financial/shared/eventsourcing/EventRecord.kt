package financial.shared.eventsourcing

import financial.shared.buildingblocks.valueobject.ValueObject
import financial.shared.valueobject.Version
import java.time.LocalDateTime

data class EventRecord(
    val code: String,
    val event: Event,
    val revision: Int,
    val sequence: Version,
    val eventName: String,
    val occurredOn: LocalDateTime,
    val aggregateId: String,
    val aggregateType: String
) : ValueObject