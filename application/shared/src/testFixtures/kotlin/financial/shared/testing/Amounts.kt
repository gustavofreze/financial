package financial.shared.testing

import financial.shared.domain.models.Amount
import financial.shared.domain.models.Currency
import java.math.BigDecimal

object Amounts {

    fun brl(value: String): Amount = Amount(value = BigDecimal(value), currency = Currency.BRL)
}
