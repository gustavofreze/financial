package financial.shared.domain.models

import financial.shared.domain.exceptions.CurrencyMismatch
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import kotlin.test.assertEquals

class AmountTest {

    @Test
    fun `plus when currencies match then sums with two decimals`() {
        /** @Given a BRL amount of 10.005 */
        val amount = Amount(value = BigDecimal("10.005"), currency = Currency.BRL)

        /** @When BRL 0.01 is added */
        val actual = amount + Amount(value = BigDecimal("0.01"), currency = Currency.BRL)

        /** @Then the sum is BRL 10.02 rounded to two decimals */
        assertEquals(Amount(value = BigDecimal("10.02"), currency = Currency.BRL), actual)
    }

    @Test
    fun `minus when currencies match then subtracts rounding half even`() {
        /** @Given a BRL amount of 10.025 */
        val amount = Amount(value = BigDecimal("10.025"), currency = Currency.BRL)

        /** @When BRL 0 is subtracted */
        val actual = amount - Amount(value = BigDecimal("0"), currency = Currency.BRL)

        /** @Then the result is BRL 10.02 rounded half even */
        assertEquals(Amount(value = BigDecimal("10.02"), currency = Currency.BRL), actual)
    }

    @Test
    fun `unary minus when negated then keeps the currency with the opposite value`() {
        /** @Given a BRL amount of 10.50 */
        val amount = Amount(value = BigDecimal("10.50"), currency = Currency.BRL)

        /** @When the amount is negated */
        val actual = -amount

        /** @Then the result is a negative BRL 10.50 */
        assertEquals(Amount(value = BigDecimal("-10.50"), currency = Currency.BRL), actual)
    }

    @Test
    fun `plus when currencies differ then fails`() {
        /** @Given a BRL amount of ten */
        val amount = Amount(value = BigDecimal.TEN, currency = Currency.BRL)

        /** @When a USD amount is added to it */
        val actual = assertThrows<CurrencyMismatch> { amount + Amount(value = BigDecimal.ONE, currency = Currency.USD) }

        /** @Then the addition fails with a currency mismatch */
        assertEquals(CurrencyMismatch::class, actual::class)
    }

    @Test
    fun `minus when currencies differ then fails`() {
        /** @Given a BRL amount of ten */
        val amount = Amount(value = BigDecimal.TEN, currency = Currency.BRL)

        /** @When a USD amount is subtracted from it */
        val actual = assertThrows<CurrencyMismatch> { amount - Amount(value = BigDecimal.ONE, currency = Currency.USD) }

        /** @Then the subtraction fails with a currency mismatch */
        assertEquals(CurrencyMismatch::class, actual::class)
    }

    @Test
    fun `value and currency when read then are the ones the amount was built with`() {
        /** @Given a USD amount of 12.34 */
        val amount = Amount(value = BigDecimal("12.34"), currency = Currency.USD)

        /** @When its value and currency are read */
        val actual = amount.value to amount.currency

        /** @Then they are the value and currency it was built with */
        assertEquals(BigDecimal("12.34") to Currency.USD, actual)
    }
}
