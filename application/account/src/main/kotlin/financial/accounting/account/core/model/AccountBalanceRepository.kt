package financial.accounting.account.core.model

interface AccountBalanceRepository {

    fun pull(number: String): AccountBalance

    fun push(accountBalance: AccountBalance)
}