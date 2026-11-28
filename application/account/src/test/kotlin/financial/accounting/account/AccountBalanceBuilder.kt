package financial.accounting.account

import financial.accounting.account.application.domain.models.AccountBalance
import financial.accounting.account.application.domain.models.AccountId
import financial.accounting.account.application.domain.models.AccountNumber
import financial.shared.domain.models.Amount
import financial.shared.domain.models.Version
import financial.shared.testing.Amounts
import java.time.OffsetDateTime

class AccountBalanceBuilder private constructor(
    private val id: AccountId,
    private val number: AccountNumber,
    private val balance: Amount,
    private val updatedAt: OffsetDateTime
) {

    fun withBalance(balance: Amount): AccountBalanceBuilder =
        AccountBalanceBuilder(id = id, number = number, balance = balance, updatedAt = updatedAt)

    fun build(): AccountBalance = AccountBalance(
        id = id,
        number = number,
        balance = balance,
        version = Version.initial(),
        updatedAt = updatedAt
    )

    companion object {
        fun debitAccount(): AccountBalanceBuilder = AccountBalanceBuilder(
            id = AccountId(value = 1),
            number = AccountNumber(value = "2.1.1.001"),
            balance = Amounts.brl(value = "100.00"),
            updatedAt = OffsetDateTime.parse("2026-01-01T00:00:00Z")
        )

        fun creditAccount(): AccountBalanceBuilder = AccountBalanceBuilder(
            id = AccountId(value = 2),
            number = AccountNumber(value = "2.2.1.001"),
            balance = Amounts.brl(value = "100.00"),
            updatedAt = OffsetDateTime.parse("2026-01-01T00:00:00Z")
        )
    }
}
