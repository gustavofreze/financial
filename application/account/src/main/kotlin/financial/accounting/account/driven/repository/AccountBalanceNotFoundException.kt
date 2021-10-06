package financial.accounting.account.driven.repository

class AccountBalanceNotFoundException(number: String) : Exception() {

    override val message: String = "Account with number $number not found"
}