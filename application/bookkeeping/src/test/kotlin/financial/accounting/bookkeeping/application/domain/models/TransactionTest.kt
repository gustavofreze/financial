package financial.accounting.bookkeeping.application.domain.models

import financial.accounting.bookkeeping.application.domain.events.TransactionRegistered
import financial.shared.domain.models.Version
import financial.shared.testing.Amounts
import financial.shared.testing.FixedClock
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TransactionTest {

    @Test
    fun `register when the transaction is new then records one registered event as the first in sequence`() {
        /** @Given a new transaction of BRL 150 from 2.1.1.001 to 2.2.1.001 */
        val id = TransactionId(value = "4fc06d14-0a42-41af-8d78-710b2a60947d")
        val amount = Amounts.brl(value = "150")
        val debitAccount = AccountNumber(value = "2.1.1.001")
        val creditAccount = AccountNumber(value = "2.2.1.001")

        /** @When the transaction is registered at the fixed instant */
        val actual = Transaction.register(
            id = id,
            amount = amount,
            occurredOn = FixedClock.now,
            debitAccount = debitAccount,
            creditAccount = creditAccount
        )

        /** @Then the transaction records one transaction registered event as the first in sequence */
        val expected = TransactionRegistered(
            id = id,
            amount = amount,
            status = Status.REGISTERED,
            debitAccount = debitAccount,
            creditAccount = creditAccount
        )
        val recorded = RecordedEvent(event = expected, sequence = Version(value = 1), occurredOn = FixedClock.now)
        assertEquals(listOf(recorded), actual.recordedEvents)

        /** @And the transaction carries its id */
        assertEquals(id, actual.id)

        /** @And the transaction carries its amount of BRL 150 */
        assertEquals(amount, actual.amount)
    }
}
