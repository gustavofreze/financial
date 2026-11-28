package financial.accounting.bookkeeping.driven.repository

import financial.accounting.bookkeeping.BookkeepingSchema
import financial.accounting.bookkeeping.StoredEvent
import financial.accounting.bookkeeping.TransactionBuilder
import financial.accounting.bookkeeping.application.exceptions.TransactionsUnavailable
import financial.shared.adapters.database.DatabaseFailure
import financial.shared.adapters.json.JacksonJsonMapper
import financial.shared.testing.FixedClock
import financial.shared.testing.H2
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertIs

@Tag("Integration")
class EventStoreTransactionsTest {

    private val database = H2.database(*BookkeepingSchema.statements)

    @Test
    fun `append when the transaction is registered then stores its event with the published payload`() {
        /** @Given a transaction of BRL 2587 registered from 2.1.1.001 to 2.2.1.001 */
        val transaction = TransactionBuilder.registered().build()

        /** @When the transaction is appended to the event store */
        EventStoreTransactions(database = database, jsonMapper = JacksonJsonMapper()).append(transaction = transaction)

        /** @Then the event store holds its transaction registered event with the published payload */
        val expected = StoredEvent(
            payload = """{"id":"4fc06d14-0a42-41af-8d78-710b2a60947d","amount":{"value":2587,"currency":"BRL"},""" +
                """"status":1,"debitAccount":"2.1.1.001","creditAccount":"2.2.1.001"}""",
            revision = 1,
            sequence = 1,
            eventName = "TransactionRegistered",
            occurredOn = FixedClock.instant,
            aggregateId = transaction.id.value,
            aggregateType = "Transaction"
        )
        assertEquals(listOf(expected), BookkeepingSchema.storedEvents(database = database))
    }

    @Test
    fun `append when the event store cannot be written then fails as unavailable`() {
        /** @Given an event store over a database with no schema */
        val transactions = EventStoreTransactions(database = H2.database(), jsonMapper = JacksonJsonMapper())

        /** @When a registered transaction is appended */
        val actual = assertThrows<TransactionsUnavailable> {
            transactions.append(transaction = TransactionBuilder.registered().build())
        }

        /** @Then the unavailable event store keeps the database failure as its cause */
        assertIs<DatabaseFailure>(actual.cause)
    }
}
