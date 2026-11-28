package financial.accounting.account.driven.troubleshoot

import financial.shared.testing.LoggerSpy
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LoggedSolvingNoticesTest {

    @Test
    fun `solved when failures went through a replay then logs how many`() {
        /** @Given a logger that records the lines it receives */
        val logger = LoggerSpy()

        /** @When two processed failures are noticed */
        LoggedSolvingNotices(logger = logger).solved(count = 2)

        /** @Then the logger records the number of processed failures as an info line */
        assertEquals(listOf("Number of processed failures: 2"), logger.infos)
    }
}
