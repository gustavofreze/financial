package financial.accounting.bookkeeping.application.domain.models

import financial.accounting.bookkeeping.application.domain.events.TransactionRegistered
import financial.shared.domain.models.Version
import java.time.OffsetDateTime

data class RecordedEvent(val event: TransactionRegistered, val sequence: Version, val occurredOn: OffsetDateTime)
