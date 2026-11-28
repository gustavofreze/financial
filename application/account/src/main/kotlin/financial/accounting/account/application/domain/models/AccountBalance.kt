package financial.accounting.account.application.domain.models

import financial.shared.domain.models.Amount
import financial.shared.domain.models.Version
import java.time.OffsetDateTime

data class AccountBalance(
    val id: AccountId,
    val number: AccountNumber,
    val balance: Amount,
    val version: Version,
    val updatedAt: OffsetDateTime
) {

    fun debit(at: OffsetDateTime, amount: Amount): AccountBalance = copy(balance = balance - amount, updatedAt = at)

    fun credit(at: OffsetDateTime, amount: Amount): AccountBalance = copy(balance = balance + amount, updatedAt = at)
}
