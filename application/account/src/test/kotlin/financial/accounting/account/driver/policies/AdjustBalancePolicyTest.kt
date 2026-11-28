package financial.accounting.account.driver.policies

import financial.accounting.account.AdjustBalanceBuilder
import financial.accounting.account.PolicyFailuresSpy
import financial.accounting.account.ReceivedEventBuilder
import financial.accounting.account.application.commands.AdjustBalance
import financial.accounting.account.application.domain.models.AccountNumber
import financial.accounting.account.application.domain.models.TransactionId
import financial.accounting.account.application.exceptions.AccountBalanceNotFound
import financial.shared.adapters.json.JacksonJsonMapper
import financial.shared.application.ReceivedEvent
import financial.shared.domain.exceptions.UnsupportedCurrency
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

class AdjustBalancePolicyTest {

    private val failures = PolicyFailuresSpy.empty()

    private val adjusted = mutableListOf<AdjustBalance>()

    private val policyName = "financial.accounting.account.driver.policies.AdjustBalancePolicy"

    private val eventName = "financial.accounting.account.driver.policies.events.TransactionRegistered"

    @Test
    fun `handle when a transaction is registered then adjusts the balances in the context terms`() {
        /** @Given a transaction registered event of BRL 30 from 2.1.1.001 to 2.2.1.001 */
        val event = ReceivedEventBuilder.transactionRegistered().build()

        /** @When the policy handles the event */
        policy(failure = null).handle(event = event)

        /** @Then the balances are adjusted by BRL 30 from the debit to the credit account under t1 */
        assertEquals(listOf(AdjustBalanceBuilder.fromDebitToCredit().build()), adjusted)

        /** @And no failure is recorded */
        assertTrue(failures.recorded.isEmpty())
    }

    @Test
    fun `handle when the event is another one then ignores it`() {
        /** @Given an event named AccountOpened */
        val event = ReceivedEvent(name = "AccountOpened", payload = "{}")

        /** @When the policy handles the event */
        policy(failure = null).handle(event = event)

        /** @Then no balance is adjusted */
        assertTrue(adjusted.isEmpty())

        /** @And no failure is recorded */
        assertTrue(failures.recorded.isEmpty())
    }

    @Test
    fun `handle when the adjustment fails then records the cause with the payload under the policy name`() {
        /** @Given a transaction registered event */
        val event = ReceivedEventBuilder.transactionRegistered().build()

        /** @And an adjustment that fails because the debit account 2.1.1.001 is not found */
        val cause = AccountBalanceNotFound(number = AccountNumber(value = "2.1.1.001"))

        /** @When the policy handles the event */
        policy(failure = cause).handle(event = event)

        /** @Then the recorded failure keeps the cause itself */
        val actual = failures.recorded.single()
        assertSame(cause, actual.cause)

        /** @And it keeps the payload as received */
        assertEquals(event.payload, actual.payload)

        /** @And it names the transaction registered event */
        assertEquals(eventName, actual.eventName)

        /** @And it names the adjust balance policy */
        assertEquals(policyName, actual.policyName)
    }

    @Test
    fun `handle when the currency is not supported then records the failure without adjusting`() {
        /** @Given a transaction registered event in EUR */
        val event = ReceivedEventBuilder.transactionRegistered().withCurrency(currency = "EUR").build()

        /** @When the policy handles the event */
        policy(failure = null).handle(event = event)

        /** @Then the recorded failure is the unsupported currency EUR */
        assertEquals("EUR", assertIs<UnsupportedCurrency>(failures.recorded.single().cause).value)

        /** @And no balance is adjusted */
        assertTrue(adjusted.isEmpty())
    }

    @Test
    fun `handle when the payload is malformed then records it instead of dropping it`() {
        /** @Given a transaction registered event whose payload is not json */
        val event = ReceivedEvent(name = "TransactionRegistered", payload = "not json")

        /** @When the policy handles the event */
        policy(failure = null).handle(event = event)

        /** @Then the recorded failure keeps the malformed payload */
        assertEquals("not json", failures.recorded.single().payload)

        /** @And no balance is adjusted */
        assertTrue(adjusted.isEmpty())
    }

    @Test
    fun `replay when the payload holds the event then adjusts the balances`() {
        /** @Given the payload of a transaction registered event under t1 */
        val payload = ReceivedEventBuilder.transactionRegistered().build().payload

        /** @When the payload is replayed through the policy */
        policy(failure = null).replay(payload = payload)

        /** @Then the balances are adjusted under the transaction t1 */
        assertEquals(TransactionId(value = "t1"), adjusted.single().transactionId)
    }

    @Test
    fun `name when read then is the name the troubleshooting queue stores`() {
        /** @When the name of the policy is read */
        val actual = policy(failure = null).name

        /** @Then the name is the one the troubleshooting queue stores */
        assertEquals(policyName, actual)
    }

    private fun policy(failure: Exception?) = AdjustBalancePolicy(
        failures = failures,
        adjusting = { command ->
            adjusted.add(command)
            failure?.let { throw it }
        },
        jsonMapper = JacksonJsonMapper()
    )
}
