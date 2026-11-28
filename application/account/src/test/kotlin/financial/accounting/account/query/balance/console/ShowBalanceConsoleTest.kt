package financial.accounting.account.query.balance.console

import financial.accounting.account.query.balance.Balance
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.math.BigDecimal
import kotlin.test.assertEquals

class ShowBalanceConsoleTest {

    @Test
    fun `run when balances are positive, negative and zero then prints the accounting table`() {
        /** @Given a console printing to a buffer over a negative, a positive and a zero balance */
        val output = ByteArrayOutputStream()
        val balances = listOf(
            Balance(value = BigDecimal("-2487.00"), number = "2.1.1.001"),
            Balance(value = BigDecimal("2687.00"), number = "2.2.1.001"),
            Balance(value = BigDecimal("0.00"), number = "2.3.1.001")
        )
        val console = ShowBalanceConsole(output = PrintStream(output, true), finding = { balances })

        /** @When the console runs */
        console.run()

        /** @Then the ShowBalance console prints each account with its balance and its sign as a status */
        val expected = """
            +--------------+--------------+--------------+
            | Account      | Balance      | Status       |
            +--------------+--------------+--------------+
            | 2.1.1.001    | (2487.00)    | NEGATIVE     |
            | 2.2.1.001    | 2687.00      | POSITIVE     |
            | 2.3.1.001    | 0.00         | ZERO         |
            +--------------+--------------+--------------+
        """.trimIndent() + System.lineSeparator()
        assertEquals("ShowBalance", console.name)
        assertEquals(expected, output.toString())
    }
}
