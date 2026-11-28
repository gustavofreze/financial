package financial.accounting.account.driver.console

import financial.accounting.account.application.commands.SolveFailures
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SolveFailuresConsoleTest {

    @Test
    fun `run when asked then solves the failures up to the default limit`() {
        /** @Given a console that records each solving it asks for */
        val handled = mutableListOf<SolveFailures>()
        val console = SolveFailuresConsole(solving = { command -> handled.add(command) })

        /** @When the console runs */
        console.run()

        /** @Then the console is named SolveFailures */
        assertEquals("SolveFailures", console.name)

        /** @And it asks to solve the failures up to the limit of 100 */
        assertEquals(listOf(SolveFailures(limit = 100)), handled)
    }
}
