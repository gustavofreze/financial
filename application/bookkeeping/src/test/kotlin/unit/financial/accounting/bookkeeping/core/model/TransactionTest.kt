package unit.financial.accounting.bookkeeping.core.model

import commonvalues.Accounts.CREDIT_ACCOUNT_IN_BRL
import commonvalues.Accounts.DEBIT_ACCOUNT_IN_BRL
import financial.accounting.bookkeeping.command.register.RegisterFinancialTransaction
import financial.accounting.bookkeeping.core.events.TransactionRegistered
import financial.accounting.bookkeeping.core.model.Status
import financial.accounting.bookkeeping.core.model.Transaction
import financial.accounting.bookkeeping.core.valueobject.TransactionId
import financial.shared.eventsourcing.EventRecord
import financial.shared.eventsourcing.EventSourcing
import financial.shared.eventsourcing.EventSourcingCapabilities
import financial.shared.valueobject.Amount
import financial.shared.valueobject.Currency
import financial.shared.valueobject.UUID
import financial.shared.valueobject.Version
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TransactionTest {

    private lateinit var eventSourcing: EventSourcing<Transaction>

    @BeforeEach
    fun `Before each`() {
        eventSourcing = EventSourcingCapabilities()
    }

    @Test
    fun `GIVEN that I have a valid transaction, when the register method is called, THEN record the transaction`() {
        val command = RegisterFinancialTransaction()
        val amount = command.amount
        val debitAccount = command.debitAccount
        val creditAccount = command.creditAccount

        val identity = TransactionId.generate()
        val transaction = Transaction(identity = identity, sequence = Version.first(), eventSourcing = eventSourcing)

        transaction.register(
            amount = amount,
            debitAccount = debitAccount,
            creditAccount = creditAccount
        )

        assertEquals(identity.value, transaction.id)
        assertEquals(amount.value, transaction.amount.value)
        assertEquals(amount.currency, transaction.amount.currency)
        assertEquals(Status.REGISTERED, transaction.status)
        assertEquals(debitAccount, transaction.debitAccount)
        assertEquals(creditAccount, transaction.creditAccount)
        assertNotNull(transaction.registrationDate)
    }

    @Test
    fun `GIVEN a transaction, WHEN the reconstitute method is called, THEN reconstitute all events that have occurred`() {
        val identity = TransactionId.generate()
        val transaction = Transaction(identity = identity, sequence = Version.first(), eventSourcing = eventSourcing)

        assertEquals(0, transaction.eventSourcing.recordedEvents.count())

        val event = TransactionRegistered(
            id = identity.value,
            amount = Amount((10..10000).random().toBigDecimal(), Currency.BRL),
            status = Status.REGISTERED.ordinal,
            debitAccount = DEBIT_ACCOUNT_IN_BRL,
            creditAccount = CREDIT_ACCOUNT_IN_BRL
        )

        val eventRecord = EventRecord(
            code = UUID.generateAsString(),
            event = event,
            revision = event.revision(),
            sequence = Version(1).next(),
            eventName = event.type(),
            occurredOn = LocalDateTime.now(),
            aggregateId = event.id,
            aggregateType = Transaction::class.java.simpleName
        )

        transaction.reconstitute(listOf(eventRecord))

        assertEquals(identity.value, transaction.id)
        assertEquals(event.amount.value, transaction.amount.value)
        assertEquals(event.amount.currency, transaction.amount.currency)
        assertEquals(Status.REGISTERED, transaction.status)
        assertEquals(event.debitAccount, transaction.debitAccount)
        assertEquals(event.creditAccount, transaction.creditAccount)
        assertNotNull(transaction.registrationDate)
        assertEquals(0, transaction.eventSourcing.recordedEvents.count())
    }
}