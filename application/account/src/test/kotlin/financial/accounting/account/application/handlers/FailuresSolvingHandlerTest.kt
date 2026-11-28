package financial.accounting.account.application.handlers

import financial.accounting.account.PolicyFailuresSpy
import financial.accounting.account.PolicyReplaySpy
import financial.accounting.account.application.commands.SolveFailures
import financial.accounting.account.application.exceptions.PolicyFailuresUnavailable
import financial.accounting.account.application.exceptions.PolicyNotFound
import financial.accounting.account.application.ports.outbound.PendingPolicyFailure
import financial.accounting.account.application.ports.outbound.PolicyFailures
import financial.accounting.account.application.ports.outbound.PolicyReplay
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

class FailuresSolvingHandlerTest {

    private val solved = mutableListOf<Int>()

    @Test
    fun `handle when the replay succeeds then resolves the failure`() {
        /** @Given a pending failure f1 of the Adjust policy */
        val failures = PolicyFailuresSpy.holding(pending(id = "f1", policyName = "Adjust"))

        /** @And an Adjust policy whose replay succeeds */
        val policy = PolicyReplaySpy(name = "Adjust", failure = null)

        /** @When the failures are solved */
        handler(failures = failures, policies = listOf(policy)).handle(command = SolveFailures())

        /** @Then the Adjust policy replayed the payload of the failure f1 */
        assertEquals(listOf("payload f1"), policy.replayed)

        /** @And the failure f1 is resolved */
        assertEquals(listOf("f1"), failures.resolved)

        /** @And no failure is retained */
        assertTrue(failures.retained.isEmpty())
    }

    @Test
    fun `handle when the replay fails again then retains the failure with its new cause`() {
        /** @Given a new cause the replay fails with */
        val cause = IllegalStateException()

        /** @And a pending failure f1 of the Adjust policy */
        val failures = PolicyFailuresSpy.holding(pending(id = "f1", policyName = "Adjust"))

        /** @When the failures are solved by an Adjust policy whose replay fails with that cause */
        handler(failures = failures, policies = listOf(PolicyReplaySpy(name = "Adjust", failure = cause)))
            .handle(command = SolveFailures())

        /** @Then no failure is resolved */
        assertTrue(failures.resolved.isEmpty())

        /** @And the failure f1 is retained */
        assertEquals("f1", failures.retained.single().first)

        /** @And it is retained with the new cause */
        assertSame(cause, failures.retained.single().second)
    }

    @Test
    fun `handle when resolving fails then propagates the fault without retaining`() {
        /** @Given a pending failure f1 of the Adjust policy whose resolution fails as unavailable */
        val fault = PolicyFailuresUnavailable(cause = IllegalStateException())
        val failures = PolicyFailuresSpy.failingToResolve(failure = fault, pending(id = "f1", policyName = "Adjust"))

        /** @And an Adjust policy whose replay succeeds */
        val handler = handler(failures = failures, policies = listOf(PolicyReplaySpy(name = "Adjust", failure = null)))

        /** @When the failures are solved */
        val actual = assertThrows<PolicyFailuresUnavailable> { handler.handle(command = SolveFailures()) }

        /** @Then the unavailability of the troubleshooting queue propagates as it was raised */
        assertSame(fault, actual)

        /** @And no failure is retained */
        assertTrue(failures.retained.isEmpty())
    }

    @Test
    fun `handle when no policy has the recorded name then retains the failure as a missing policy`() {
        /** @Given a pending failure f1 recorded under the policy unknown.Policy */
        val failures = PolicyFailuresSpy.holding(pending(id = "f1", policyName = "unknown.Policy"))

        /** @When the failures are solved with no policy registered */
        handler(failures = failures, policies = emptyList()).handle(command = SolveFailures())

        /** @Then the failure f1 is retained */
        assertEquals("f1", failures.retained.single().first)

        /** @And its cause is the missing policy unknown.Policy */
        assertEquals("unknown.Policy", assertIs<PolicyNotFound>(failures.retained.single().second).policyName)
    }

    @Test
    fun `handle when failures are pending then reads up to the limit and notifies how many were processed`() {
        /** @Given two pending failures f1 and f2 of the Adjust policy */
        val failures = PolicyFailuresSpy.holding(
            pending(id = "f1", policyName = "Adjust"),
            pending(id = "f2", policyName = "Adjust")
        )

        /** @When the failures are solved by an Adjust policy whose replay succeeds */
        handler(failures = failures, policies = listOf(PolicyReplaySpy(name = "Adjust", failure = null)))
            .handle(command = SolveFailures())

        /** @Then the pending failures are read up to 100 */
        assertEquals(100, failures.requestedLimit)

        /** @And the notice tells that two failures were processed */
        assertEquals(listOf(2), solved)
    }

    private fun pending(id: String, policyName: String) =
        PendingPolicyFailure(id = id, payload = "payload $id", policyName = policyName)

    private fun handler(failures: PolicyFailures, policies: List<PolicyReplay>) = FailuresSolvingHandler(
        notices = { count -> solved.add(count) },
        failures = failures,
        policies = policies
    )
}
