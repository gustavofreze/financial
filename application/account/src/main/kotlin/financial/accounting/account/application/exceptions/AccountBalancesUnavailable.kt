package financial.accounting.account.application.exceptions

class AccountBalancesUnavailable(override val cause: Exception) : IllegalStateException()
