package financial.accounting.account.driver.policies

import financial.accounting.account.PolicyFailuresSpy
import financial.accounting.account.ReceivedEventBuilder
import financial.accounting.account.application.commands.AdjustBalance
import financial.shared.adapters.json.JacksonJsonMapper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PolicyEventBusTest {

    @Test
    fun `dispatch when policies are registered then tells each one about the event`() {
        /** @Given an adjust balance policy that records each adjustment it raises */
        val adjusted = mutableListOf<AdjustBalance>()
        val policy = AdjustBalancePolicy(
            failures = PolicyFailuresSpy.empty(),
            adjusting = { command -> adjusted.add(command) },
            jsonMapper = JacksonJsonMapper()
        )

        /** @When a transaction registered event is dispatched to that policy registered twice */
        PolicyEventBus(policies = listOf(policy, policy))
            .dispatch(event = ReceivedEventBuilder.transactionRegistered().build())

        /** @Then two adjustments are raised */
        assertEquals(2, adjusted.size)
    }
}
