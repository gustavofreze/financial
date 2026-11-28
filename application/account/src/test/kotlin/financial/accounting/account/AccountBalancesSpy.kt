package financial.accounting.account

import financial.accounting.account.application.domain.models.AccountBalance
import financial.accounting.account.application.domain.models.AccountNumber
import financial.accounting.account.application.domain.models.TransactionId
import financial.accounting.account.application.ports.outbound.AccountBalances

class AccountBalancesSpy private constructor(private val balances: List<AccountBalance>) : AccountBalances {

    val adjusted = mutableListOf<Triple<AccountBalance, AccountBalance, TransactionId>>()

    override fun find(number: AccountNumber): AccountBalance? = balances.firstOrNull { it.number == number }

    override fun adjust(debited: AccountBalance, credited: AccountBalance, transactionId: TransactionId) {
        adjusted.add(Triple(debited, credited, transactionId))
    }

    companion object {
        fun holding(vararg balances: AccountBalance) = AccountBalancesSpy(balances = balances.toList())
    }
}
