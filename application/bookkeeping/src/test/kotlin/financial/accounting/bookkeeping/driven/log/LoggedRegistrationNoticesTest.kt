package financial.accounting.bookkeeping.driven.log

import financial.accounting.bookkeeping.TransactionBuilder
import financial.shared.testing.LoggerSpy
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LoggedRegistrationNoticesTest {

    @Test
    fun `registered when a transaction was registered then logs its value`() {
        /** @Given a logger that records the lines it receives */
        val logger = LoggerSpy()

        /** @And a registered transaction of BRL 2587 */
        val transaction = TransactionBuilder.registered().build()

        /** @When the registration of the transaction is noticed */
        LoggedRegistrationNotices(logger = logger).registered(transaction = transaction)

        /** @Then the logger records the value of the transaction as an info line */
        assertEquals(listOf("Transaction with value 2587 has been registered"), logger.infos)
    }
}
