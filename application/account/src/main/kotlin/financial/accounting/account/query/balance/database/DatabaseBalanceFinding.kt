package financial.accounting.account.query.balance.database

import financial.accounting.account.query.balance.Balance
import financial.accounting.account.query.balance.BalanceFinding
import financial.accounting.account.query.balance.BalanceFindingUnavailable
import financial.shared.adapters.database.Database
import financial.shared.adapters.database.TranslatedDatabase

class DatabaseBalanceFinding(database: Database) : BalanceFinding {

    private val mapper = BalanceMapper()

    private val database = TranslatedDatabase(
        database = database,
        translate = { failure -> BalanceFindingUnavailable(cause = failure) }
    )

    override fun findAll(): List<Balance> =
        database.fetchAll(sql = Queries.FIND_ALL, mapper = mapper, parameters = emptyMap())
}
