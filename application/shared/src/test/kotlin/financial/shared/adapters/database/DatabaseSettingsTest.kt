package financial.shared.adapters.database

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DatabaseSettingsTest {

    @Test
    fun `pool when built then carries the settings with one idle connection and at most four`() {
        /** @Given settings with a user, a password and a jdbc url */
        val jdbcUrl = "jdbc:postgresql://db:5432/financial"
        val settings = DatabaseSettings(user = "app", jdbcUrl = jdbcUrl, password = "secret")

        /** @When the connection pool is built */
        val actual = settings.pool()

        /** @Then the pool carries the user and the password */
        assertEquals("app", actual.username)
        assertEquals("secret", actual.password)

        /** @And the pool points at the jdbc url */
        assertEquals("jdbc:postgresql://db:5432/financial", actual.jdbcUrl)

        /** @And the pool keeps one idle connection */
        assertEquals(1, actual.minimumIdle)

        /** @And the pool holds at most four connections */
        assertEquals(4, actual.maximumPoolSize)
    }
}
