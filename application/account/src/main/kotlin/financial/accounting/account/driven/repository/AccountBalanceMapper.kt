package financial.accounting.account.driven.repository

import financial.accounting.account.core.model.AccountBalance
import financial.accounting.account.core.valueobject.AccountId
import financial.shared.databases.DatabaseContext
import financial.shared.databases.DatabaseMapper
import financial.shared.valueobject.Amount
import financial.shared.valueobject.Currency
import java.sql.ResultSet

class AccountBalanceMapper : DatabaseMapper<AccountBalance> {

    override fun map(resultSet: ResultSet, context: DatabaseContext): AccountBalance {
        val currency = Currency.valueOf(resultSet.getString("currency"))

        return AccountBalance(
            identity = AccountId(resultSet.getInt("id")),
            number = resultSet.getString("number"),
            balance = Amount(resultSet.getBigDecimal("balance"), currency),
            updatedAt = resultSet.getTimestamp("updated_at").toLocalDateTime()
        )
    }
}