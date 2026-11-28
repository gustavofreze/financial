package financial.accounting.account.application.exceptions

import financial.accounting.account.application.domain.models.AccountNumber

class AccountBalanceNotFound(val number: AccountNumber) : IllegalStateException()
