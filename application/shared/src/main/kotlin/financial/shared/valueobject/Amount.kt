package financial.shared.valueobject

import financial.shared.buildingblocks.valueobject.ValueObject
import java.math.BigDecimal
import java.math.RoundingMode

data class Amount(val value: BigDecimal, val currency: Currency) : ValueObject {

    private companion object {
        const val ERROR = "Currencies cannot be different"
    }

    operator fun plus(amount: Amount): Amount {
        validateCurrency(amount)
        return Amount(value = value + amount.value, currency = currency).applyScale()
    }

    operator fun minus(amount: Amount): Amount {
        validateCurrency(amount)
        return Amount(value = value - amount.value, currency = currency).applyScale()
    }

    fun valueIsZero(): Boolean = value == BigDecimal.ZERO

    fun valueIsNegative(): Boolean = value < BigDecimal.ZERO

    private fun applyScale(): Amount {
        return Amount(value = value.setScale(2, RoundingMode.HALF_EVEN), currency = currency)
    }

    private fun validateCurrency(amount: Amount) {
        if (currency != amount.currency) error(ERROR)
    }
}