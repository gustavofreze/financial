package financial.accounting.account.query

import financial.accounting.account.core.model.AccountBalance
import financial.accounting.account.driven.repository.AccountBalanceMapper
import financial.accounting.account.query.Queries.FIND
import financial.shared.databases.RelationalDatabase

class BalanceDao(private val database: RelationalDatabase, private val accountBalanceMapper: AccountBalanceMapper) {

    fun findAll(): List<AccountBalance> {
        return database
            .query(FIND)
            .fetchAll(accountBalanceMapper)
    }
}