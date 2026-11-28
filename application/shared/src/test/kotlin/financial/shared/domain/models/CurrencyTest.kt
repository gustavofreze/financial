package financial.shared.domain.models

import financial.shared.domain.exceptions.UnsupportedCurrency
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class CurrencyTest {

    @Test
    fun `from when the code is supported then is that currency`() {
        /** @Given a supported currency code */
        val code = "BRL"

        /** @When the currency is created from the code */
        val actual = Currency.from(code = code)

        /** @Then the currency is BRL */
        assertEquals(Currency.BRL, actual)
    }

    @Test
    fun `from when the code is not supported then fails with the code`() {
        /** @Given the code of an unsupported currency */
        val code = "EUR"

        /** @When the currency is created from the code */
        val actual = assertThrows<UnsupportedCurrency> { Currency.from(code = code) }

        /** @Then the failure carries the code */
        assertEquals("EUR", actual.value)
    }
}
