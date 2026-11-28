package financial.accounting.account.driven.troubleshoot

import financial.accounting.account.AccountSchema
import financial.accounting.account.StoredPolicyFailure
import financial.accounting.account.application.exceptions.PolicyFailuresUnavailable
import financial.accounting.account.application.exceptions.PolicyNotFound
import financial.accounting.account.application.ports.outbound.PendingPolicyFailure
import financial.accounting.account.application.ports.outbound.PolicyFailure
import financial.shared.adapters.database.Database
import financial.shared.adapters.database.DatabaseFailure
import financial.shared.testing.FixedClock
import financial.shared.testing.H2
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.OffsetDateTime
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@Tag("Integration")
class DatabasePolicyFailuresTest {

    private val database = H2.database(*AccountSchema.statements)

    private val failures = policyFailures(database = database)

    private val payload = """{"id":"t1"}"""

    private val policyName = "financial.accounting.account.driver.policies.AdjustBalancePolicy"

    private val eventName = "financial.accounting.account.driver.policies.events.TransactionRegistered"

    @Test
    fun `record when a policy failed then stores the payload, the event, the policy and the described cause`() {
        /** @Given a failure of the adjust balance policy caused by the policy unknown.Policy not being found */
        val failure = failure(cause = PolicyNotFound(policyName = "unknown.Policy"))

        /** @When the failure is recorded */
        failures.record(failure = failure)

        /** @Then the troubleshooting queue stores the payload, the event, the policy and the described cause */
        val expected = StoredPolicyFailure(
            error = "Policy unknown.Policy not found",
            resent = 0,
            payload = payload,
            eventName = eventName,
            policyName = policyName
        )
        assertEquals(listOf(expected), AccountSchema.storedFailures(database = database))
    }

    @Test
    fun `pending when failures were recorded then returns the payload under the policy name`() {
        /** @Given a recorded failure of the adjust balance policy */
        failures.record(failure = failure(cause = IllegalStateException()))

        /** @When the pending failures are read up to 100 */
        val actual = failures.pending(limit = 100).single()

        /** @Then the pending failure carries the recorded payload */
        assertEquals(payload, actual.payload)

        /** @And it is listed under the name of the adjust balance policy */
        assertEquals(policyName, actual.policyName)
    }

    @Test
    fun `pending when more failures exist than the limit then returns the oldest up to the limit`() {
        /** @Given the failures f2, f1 and f3 stored out of the order they were created in */
        val seeded = H2.database(
            *AccountSchema.statements,
            AccountSchema.failure(id = "f2", createdAt = OffsetDateTime.parse("2026-01-02T00:00:00Z")),
            AccountSchema.failure(id = "f1", createdAt = OffsetDateTime.parse("2026-01-01T00:00:00Z")),
            AccountSchema.failure(id = "f3", createdAt = OffsetDateTime.parse("2026-01-03T00:00:00Z"))
        )

        /** @When the pending failures are read up to 2 */
        val actual = policyFailures(database = seeded).pending(limit = 2)

        /** @Then the two oldest failures f1 and f2 come back in the order they were created in */
        assertEquals(listOf("f1", "f2"), actual.map(PendingPolicyFailure::id))
    }

    @Test
    fun `resolve when the failure was replayed then leaves the pending list`() {
        /** @Given a troubleshooting queue holding the failure f1 */
        val queue = policyFailures(database = withOneFailure())

        /** @When the failure f1 is resolved */
        queue.resolve(id = "f1")

        /** @Then no failure is pending */
        assertTrue(queue.pending(limit = 100).isEmpty())
    }

    @Test
    fun `retain when the replay failed again then keeps it pending with the described new cause`() {
        /** @Given a troubleshooting queue holding the failure f1 */
        val seeded = withOneFailure()
        val queue = policyFailures(database = seeded)

        /** @When the failure f1 is retained because the policy unknown.Policy is not found */
        queue.retain(id = "f1", cause = PolicyNotFound(policyName = "unknown.Policy"))

        /** @Then the stored failure describes the new cause */
        assertEquals("Policy unknown.Policy not found", AccountSchema.storedFailures(database = seeded).single().error)

        /** @And the failure f1 stays pending */
        assertEquals(listOf("f1"), queue.pending(limit = 100).map(PendingPolicyFailure::id))
    }

    @Test
    fun `pending when the queue cannot be read then fails as unavailable`() {
        /** @Given a troubleshooting queue over a database with no schema */
        val unavailable = policyFailures(database = H2.database())

        /** @When the pending failures are read */
        val actual = assertThrows<PolicyFailuresUnavailable> { unavailable.pending(limit = 1) }

        /** @Then the unavailable queue keeps the database failure as its cause */
        assertIs<DatabaseFailure>(actual.cause)
    }

    private fun policyFailures(database: Database) =
        DatabasePolicyFailures(clock = FixedClock.clock, database = database, messages = FailureMessages())

    private fun failure(cause: Exception) =
        PolicyFailure(cause = cause, payload = payload, eventName = eventName, policyName = policyName)

    private fun withOneFailure() = H2.database(
        *AccountSchema.statements,
        AccountSchema.failure(id = "f1", createdAt = OffsetDateTime.parse("2026-01-01T00:00:00Z"))
    )
}
