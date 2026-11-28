package financial.accounting.account.driven.repository

import financial.accounting.account.application.domain.models.AccountBalance
import financial.accounting.account.application.domain.models.AccountId
import financial.accounting.account.application.domain.models.AccountNumber
import financial.shared.adapters.database.Row
import financial.shared.adapters.database.RowMapper
import financial.shared.domain.models.Amount
import financial.shared.domain.models.Currency
import financial.shared.domain.models.Version

class AccountBalanceMapper : RowMapper<AccountBalance> {

    override fun map(row: Row) = AccountBalance(
        id = AccountId(value = row.integer(column = "id")),
        number = AccountNumber(value = row.text(column = "number")),
        balance = Amount(
            value = row.decimal(column = "balance"),
            currency = Currency.from(code = row.text(column = "currency"))
        ),
        version = Version(value = row.long(column = "version")),
        updatedAt = row.dateTime(column = "updated_at")
    )
}
