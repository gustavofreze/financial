package financial.shared.adapters.console

import financial.shared.ConsoleCommandSpy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ConsoleDriverTest {

    @Test
    fun `drive when the command is known then runs only that command once`() {
        /** @Given a deposit command and a withdraw command */
        val deposit = ConsoleCommandSpy(name = "Deposit")
        val withdraw = ConsoleCommandSpy(name = "Withdraw")

        /** @When the deposit command is driven */
        ConsoleDriver(commands = listOf(deposit, withdraw)).drive(name = "Deposit")

        /** @Then the deposit command runs once */
        assertEquals(1, deposit.runs)

        /** @And the withdraw command never runs */
        assertEquals(0, withdraw.runs)
    }

    @Test
    fun `drive when the command is unknown then fails naming it`() {
        /** @Given a driver with no commands */
        val driver = ConsoleDriver(commands = emptyList())

        /** @When an unknown command is driven */
        val actual = assertThrows<IllegalStateException> { driver.drive(name = "Withdraw") }

        /** @Then the failure names the unknown command */
        assertEquals("Invalid console command Withdraw", actual.message)
    }
}
