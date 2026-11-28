package financial.accounting.bookkeeping.application.domain.models

import financial.accounting.bookkeeping.application.domain.events.TransactionRegistered
import financial.shared.domain.models.Amount
import financial.shared.domain.models.Version
import java.time.OffsetDateTime

class Transaction private constructor(recorded: RecordedEvent, val recordedEvents: List<RecordedEvent>) {

    val id: TransactionId = recorded.event.id
    val amount: Amount = recorded.event.amount

    companion object {
        fun register(
            id: TransactionId,
            amount: Amount,
            occurredOn: OffsetDateTime,
            debitAccount: AccountNumber,
            creditAccount: AccountNumber
        ): Transaction {
            val event = TransactionRegistered(
                id = id,
                amount = amount,
                status = Status.REGISTERED,
                debitAccount = debitAccount,
                creditAccount = creditAccount
            )
            val recorded = RecordedEvent(event = event, sequence = Version.initial().next(), occurredOn = occurredOn)

            return Transaction(recorded = recorded, recordedEvents = listOf(recorded))
        }
    }
}
