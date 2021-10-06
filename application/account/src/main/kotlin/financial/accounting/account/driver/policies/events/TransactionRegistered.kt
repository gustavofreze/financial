package financial.accounting.account.driver.policies.events

import financial.shared.valueobject.Amount

data class TransactionRegistered(
    val id: String,
    val amount: Amount,
    val debitAccount: String,
    val creditAccount: String
) : Event