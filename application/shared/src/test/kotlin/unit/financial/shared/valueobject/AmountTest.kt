package unit.financial.shared.valueobject

import financial.shared.valueobject.Amount
import financial.shared.valueobject.Currency
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.util.stream.Stream
import kotlin.test.assertEquals

class AmountTest {

    @ParameterizedTest
    @MethodSource("amountIsZeroProvider")
    fun `GIVEN a valid Amount object, WHEN calling the isZero method, THEN check that the value is zero and return a boolean`(
        value: BigDecimal,
        expected: Boolean
    ) {
        val amount = Amount(value = value, currency = Currency.BRL)
        val actual = amount.valueIsZero()

        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @MethodSource("amountIsNegativeProvider")
    fun `GIVEN a valid Amount object, WHEN calling the isNegative method, THEN check that the value is zero and return a boolean`(
        value: BigDecimal,
        expected: Boolean
    ) {
        val amount = Amount(value = value, currency = Currency.BRL)
        val actual = amount.valueIsNegative()

        assertEquals(expected, actual)
    }

    @Test
    fun `GIVEN a valid Amount object, WHEN calling the plus method, THEN perform a plus`() {
        val amount = Amount(value = BigDecimal(999.98), currency = Currency.BRL)
        val actual = amount.plus(Amount(BigDecimal(0.02), Currency.BRL))

        val expected = Amount(value = BigDecimal(1000), currency = Currency.BRL)

        assertEquals(expected.value.setScale(2), actual.value)
        assertEquals(expected.currency, actual.currency)
    }

    @Test
    fun `GIVEN a valid Amount object, WHEN calling the minus method, THEN perform a minus`() {
        val amount = Amount(value = BigDecimal(0.01), currency = Currency.BRL)
        val actual = amount.minus(Amount(BigDecimal(0.01), Currency.BRL))

        val expected = Amount(value = BigDecimal.ZERO, currency = Currency.BRL)

        assertEquals(expected.value.setScale(2), actual.value)
        assertEquals(expected.currency, actual.currency)
    }

    @Test
    fun `GIVEN i have an plus operation, WHEN plus method is called, WHERE the coins do not equal, THEN return an error`() {
        val amountBRL = Amount(value = BigDecimal(1.0), currency = Currency.BRL)
        val amountUSD = Amount(value = BigDecimal(10.0), currency = Currency.USD)

        assertThrows<Exception>("Currencies cannot be different") {
            amountBRL.plus(amountUSD)
        }
    }

    companion object {
        @JvmStatic
        fun amountIsZeroProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(BigDecimal.ZERO, true),
                Arguments.of(BigDecimal(1.00), false),
                Arguments.of(BigDecimal(-1.00), false),
            )
        }

        @JvmStatic
        fun amountIsNegativeProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(BigDecimal.ZERO, false),
                Arguments.of(BigDecimal(-9.99), true),
                Arguments.of(BigDecimal(9.99), false),
            )
        }
    }
}