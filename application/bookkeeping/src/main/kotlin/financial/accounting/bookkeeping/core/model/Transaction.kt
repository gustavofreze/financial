package financial.accounting.bookkeeping.core.model

import financial.accounting.bookkeeping.core.events.TransactionRegistered
import financial.shared.eventsourcing.EventRecord
import financial.shared.eventsourcing.EventSourcing
import financial.shared.eventsourcing.EventSourcingRoot
import financial.shared.valueobject.Amount
import financial.shared.buildingblocks.entity.Identity
import financial.shared.valueobject.Version
import java.time.LocalDateTime

data class Transaction(
    override val identity: Identity,
    val sequence: Version,
    val eventSourcing: EventSourcing<Transaction>
) : EventSourcingRoot<Transaction>, EventSourcing<Transaction> by eventSourcing {

    lateinit var id: String
    lateinit var status: Status
    lateinit var amount: Amount
    lateinit var debitAccount: String
    lateinit var creditAccount: String
    lateinit var registrationDate: LocalDateTime

    override fun reconstitute(recordedEvents: List<EventRecord>) {
        recordedEvents.forEach { applyEvent(it, this) }
    }

    fun register(amount: Amount, debitAccount: String, creditAccount: String) {
        TransactionRegistered(
            id = identity.value.toString(),
            amount = amount,
            status = Status.REGISTERED.ordinal,
            debitAccount = debitAccount,
            creditAccount = creditAccount
        ).also { onEvent(it, identity, sequence, this) }
    }

    @Suppress("UNUSED")
    fun onTransactionRegistered(transactionRegistered: TransactionRegistered) {
        id = transactionRegistered.id
        status = Status.REGISTERED
        amount = transactionRegistered.amount
        debitAccount = transactionRegistered.debitAccount
        creditAccount = transactionRegistered.creditAccount
        registrationDate = LocalDateTime.now()
    }
}