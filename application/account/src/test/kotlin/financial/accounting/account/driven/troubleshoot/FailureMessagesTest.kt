package financial.accounting.account.driven.troubleshoot

import financial.accounting.account.application.domain.exceptions.InvalidAccountId
import financial.accounting.account.application.domain.models.AccountNumber
import financial.accounting.account.application.exceptions.AccountBalanceNotFound
import financial.accounting.account.application.exceptions.AccountBalancesUnavailable
import financial.accounting.account.application.exceptions.BalanceAdjustmentConflict
import financial.accounting.account.application.exceptions.PolicyNotFound
import financial.shared.domain.exceptions.CurrencyMismatch
import financial.shared.domain.exceptions.UnsupportedCurrency
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FailureMessagesTest {

    private val messages = FailureMessages()

    @ParameterizedTest
    @MethodSource("knownFailures")
    fun `describe when the failure is known then words it in business terms`(failure: Exception, expected: String) {
        /** @When the known failure is described */
        val actual = messages.describe(failure = failure)

        /** @Then the description words it in business terms */
        assertEquals(expected, actual)
    }

    @Test
    fun `describe when the balances are unavailable then words it and keeps the trace of the cause`() {
        /** @Given account balances unavailable because the database is down */
        val failure = AccountBalancesUnavailable(cause = IllegalStateException("database is down"))

        /** @When the failure is described */
        val actual = messages.describe(failure = failure)

        /** @Then the description words the unavailability followed by the trace of its cause */
        val expected = "Account balances are unavailable\njava.lang.IllegalStateException: database is down"
        assertTrue(actual.startsWith(expected))
    }

    @Test
    fun `describe when the failure is not known then keeps its full trace`() {
        /** @Given an unknown failure saying the database is down */
        val failure = IllegalStateException("database is down")

        /** @When the failure is described */
        val actual = messages.describe(failure = failure)

        /** @Then the description keeps the full trace of the failure */
        assertTrue(actual.startsWith("java.lang.IllegalStateException: database is down"))
    }

    private companion object {
        @JvmStatic
        fun knownFailures() = listOf(
            Arguments.of(PolicyNotFound(policyName = "unknown.Policy"), "Policy unknown.Policy not found"),
            Arguments.of(CurrencyMismatch(), "Currencies cannot be different"),
            Arguments.of(InvalidAccountId(value = 0), "Account id 0 is not valid"),
            Arguments.of(UnsupportedCurrency(value = "EUR"), "Currency EUR is not supported"),
            Arguments.of(
                AccountBalanceNotFound(number = AccountNumber(value = "2.1.1.001")),
                "Account with number 2.1.1.001 not found"
            ),
            Arguments.of(
                BalanceAdjustmentConflict(number = AccountNumber(value = "2.1.1.001")),
                "Balance of account 2.1.1.001 changed during the adjustment"
            )
        )
    }
}
