package financial.accounting.account.query.balance.database

import financial.accounting.account.query.balance.Balance
import financial.shared.adapters.database.Row
import financial.shared.adapters.database.RowMapper

class BalanceMapper : RowMapper<Balance> {

    override fun map(row: Row) = Balance(
        value = row.decimal(column = "balance"),
        number = row.text(column = "number")
    )
}
