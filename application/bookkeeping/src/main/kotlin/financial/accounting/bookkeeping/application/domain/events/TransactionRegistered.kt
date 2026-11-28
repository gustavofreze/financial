package financial.accounting.bookkeeping.application.domain.events

import financial.accounting.bookkeeping.application.domain.models.AccountNumber
import financial.accounting.bookkeeping.application.domain.models.Status
import financial.accounting.bookkeeping.application.domain.models.TransactionId
import financial.shared.domain.events.DomainEvent
import financial.shared.domain.models.Amount

data class TransactionRegistered(
    val id: TransactionId,
    val amount: Amount,
    val status: Status,
    val debitAccount: AccountNumber,
    val creditAccount: AccountNumber
) : DomainEvent {

    override val revision = 1
}
