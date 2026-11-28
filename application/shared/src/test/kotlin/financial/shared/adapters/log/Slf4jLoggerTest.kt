package financial.shared.adapters.log

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertTrue

class Slf4jLoggerTest {

    private val original = System.err

    @AfterEach
    fun restore() = System.setErr(original)

    @Test
    fun `info when called then writes the message at the info level under the owner name`() {
        /** @Given the error stream captured in a buffer */
        val output = ByteArrayOutputStream()
        System.setErr(PrintStream(output, true))

        /** @And a logger owned by this test class */
        val logger = Slf4jLogger(owner = Slf4jLoggerTest::class)

        /** @When an info message is logged */
        logger.info(message = "registered")

        /** @Then the buffer holds the message at the info level under the owner name */
        assertTrue(output.toString().contains("INFO financial.shared.adapters.log.Slf4jLoggerTest - registered"))
    }
}
