package financial.accounting.account.query.balance.console

import financial.accounting.account.query.balance.Balance
import java.util.Locale

@JvmInline
value class BalanceRow(private val balance: Balance) {

    val number: String
        get() = balance.number

    val display: String
        get() {
            val formatted = "%.2f".format(Locale.ROOT, balance.value.abs())
            return if (balance.value.signum() < 0) "($formatted)" else formatted
        }

    val status: String
        get() = when (balance.value.signum()) {
            0 -> "ZERO"
            -1 -> "NEGATIVE"
            else -> "POSITIVE"
        }
}
