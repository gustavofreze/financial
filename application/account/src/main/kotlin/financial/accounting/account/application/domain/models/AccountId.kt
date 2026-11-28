package financial.accounting.account.application.domain.models

import financial.accounting.account.application.domain.exceptions.InvalidAccountId

data class AccountId(val value: Int) {

    init {
        if (value < 1) {
            throw InvalidAccountId(value = value)
        }
    }
}
