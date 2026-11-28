package financial.shared.domain.models

import financial.shared.domain.exceptions.InvalidVersion
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class VersionTest {

    @Test
    fun `next when version is initial then is one`() {
        /** @Given the initial version */
        val version = Version.initial()

        /** @When the next version is taken */
        val actual = version.next()

        /** @Then it is version one */
        assertEquals(Version(value = 1), actual)
    }

    @Test
    fun `next when version is any value then is the following one`() {
        /** @Given version five */
        val version = Version(value = 5)

        /** @When the next version is taken */
        val actual = version.next()

        /** @Then it is version six */
        assertEquals(6L, actual.value)
    }

    @Test
    fun `initial when read then is zero`() {
        /** @When the initial version is read */
        val actual = Version.initial()

        /** @Then its value is zero */
        assertEquals(0L, actual.value)
    }

    @Test
    fun `create when value is negative then fails with the value`() {
        /** @Given a negative value */
        val value = -1L

        /** @When a version is created from it */
        val actual = assertThrows<InvalidVersion> { Version(value = value) }

        /** @Then the failure carries the negative value */
        assertEquals(-1L, actual.value)
    }
}
