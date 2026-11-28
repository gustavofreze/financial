package financial.shared

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class EnvironmentTest {

    @Test
    fun `get when the variable is set then reads its value`() {
        /** @Given the name of a variable the process carries */
        val variable = System.getenv().keys.first()

        /** @When the variable is read */
        val actual = Environment.get(variable = variable)

        /** @Then its value is the one the process carries */
        assertEquals(System.getenv(variable), actual)
    }

    @Test
    fun `get when the variable is missing then fails naming it`() {
        /** @When a variable the process lacks is read */
        val actual = assertThrows<IllegalStateException> { Environment.get(variable = "FINANCIAL_MISSING_VARIABLE") }

        /** @Then the failure names the missing variable */
        assertEquals("Environment variable FINANCIAL_MISSING_VARIABLE is missing", actual.message)
    }
}
