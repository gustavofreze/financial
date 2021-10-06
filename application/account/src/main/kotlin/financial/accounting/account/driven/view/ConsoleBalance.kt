package financial.accounting.account.driven.view

import financial.accounting.account.core.model.AccountBalance
import financial.shared.valueobject.Amount

class ConsoleBalance : Viewer {

    private companion object {
        const val ZERO = "ZERO"
        const val POSITIVE = "POSITIVE"
        const val NEGATIVE = "NEGATIVE"
        const val TABLE_FORMAT = "+--------------+--------------+--------------+%n"
        const val LEFT_ALIGN_FORMAT = "| %-12s | %-12s | %-12s |%n"
    }

    override fun show(accountBalances: List<AccountBalance>) {
        System.out.format(TABLE_FORMAT)
        System.out.format("| Account      | Balance      | Status       |%n")
        System.out.format(TABLE_FORMAT)

        accountBalances.forEach { accountBalance ->
            val status = buildStatus(accountBalance.balance)
            val account = accountBalance.number
            val balanceValue = balanceForDisplay(accountBalance.balance)

            System.out.format(LEFT_ALIGN_FORMAT, account, balanceValue, status)
        }

        System.out.format(TABLE_FORMAT)
    }

    private fun balanceForDisplay(amount: Amount): String {
        val value = amount.value

        if (amount.valueIsNegative()) {
            val format = "%.2f".format(value.abs())
            return "($format)"
        }

        return "%.2f".format(value)
    }

    private fun buildStatus(balance: Amount): String {
        return when {
            balance.valueIsZero() -> ZERO
            balance.valueIsNegative() -> NEGATIVE
            else -> POSITIVE
        }
    }
}