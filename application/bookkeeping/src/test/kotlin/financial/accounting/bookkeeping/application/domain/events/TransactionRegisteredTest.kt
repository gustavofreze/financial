package financial.accounting.bookkeeping.application.domain.events

import financial.accounting.bookkeeping.TransactionBuilder
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TransactionRegisteredTest {

    @Test
    fun `revision when the event is transaction registered then is one`() {
        /** @Given the transaction registered event recorded by a registered transaction */
        val event = TransactionBuilder.registered().build().recordedEvents.single().event

        /** @When the revision of the event is read */
        val actual = event.revision

        /** @Then the revision is 1 */
        assertEquals(1, actual)
    }
}
