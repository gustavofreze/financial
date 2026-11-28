package financial.shared.domain.models

import financial.shared.domain.exceptions.CurrencyMismatch
import java.math.BigDecimal
import java.math.RoundingMode

data class Amount(val value: BigDecimal, val currency: Currency) {

    operator fun plus(amount: Amount): Amount {
        if (currency != amount.currency) {
            throw CurrencyMismatch()
        }

        return Amount(value = (value + amount.value).setScale(SCALE, RoundingMode.HALF_EVEN), currency = currency)
    }

    operator fun minus(amount: Amount): Amount = this + -amount

    operator fun unaryMinus(): Amount = copy(value = -value)

    private companion object {
        const val SCALE = 2
    }
}
