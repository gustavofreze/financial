package financial.accounting.bookkeeping.driver.console

import financial.accounting.bookkeeping.application.commands.RegisterFinancialTransaction
import financial.accounting.bookkeeping.application.domain.models.AccountNumber
import financial.shared.domain.models.Currency
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RegisterFinancialTransactionConsoleTest {

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    fun `run when asked then registers a whole amount between the fixed accounts`(seed: Int) {
        /** @Given a console that registers through a seeded random and records each registration */
        val registered = mutableListOf<RegisterFinancialTransaction>()
        val console = RegisterFinancialTransactionConsole(
            random = Random(seed = seed),
            registering = { command -> registered.add(command) }
        )

        /** @When the console runs */
        console.run()

        /** @Then the console is named RegisterFinancialTransaction */
        val actual = registered.single()
        assertEquals("RegisterFinancialTransaction", console.name)

        /** @And the amount is between 10 and 10000 */
        assertTrue(actual.amount.value in BigDecimal(10)..BigDecimal(10000))

        /** @And the amount is a whole number */
        assertEquals(0, actual.amount.value.scale())

        /** @And the amount is in BRL */
        assertEquals(Currency.BRL, actual.amount.currency)

        /** @And the debit account is 2.1.1.001 */
        assertEquals(AccountNumber(value = "2.1.1.001"), actual.debitAccount)

        /** @And the credit account is 2.2.1.001 */
        assertEquals(AccountNumber(value = "2.2.1.001"), actual.creditAccount)
    }

    @Test
    fun `run when the default random is used then registers an amount within the bounds`() {
        /** @Given a list that records each registration */
        val registered = mutableListOf<RegisterFinancialTransaction>()

        /** @When a console on the default random runs */
        RegisterFinancialTransactionConsole(registering = { command -> registered.add(command) }).run()

        /** @Then the registered amount is between 10 and 10000 */
        assertTrue(registered.single().amount.value in BigDecimal(10)..BigDecimal(10000))
    }
}
