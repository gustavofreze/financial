package financial.accounting.account.driven.repository

import financial.accounting.account.core.model.AccountBalance
import financial.accounting.account.driven.repository.Queries.FIND
import financial.accounting.account.driven.repository.Queries.UPDATE
import financial.shared.databases.RelationalDatabase
import financial.accounting.account.core.model.AccountBalanceRepository as IAccountBalanceRepository

class AccountBalanceRepository(
    private val database: RelationalDatabase,
    private val accountBalanceMapper: AccountBalanceMapper
) : IAccountBalanceRepository {

    override fun pull(number: String): AccountBalance {
        return database
            .bind("number", number)
            .query(FIND)
            .fetchOne(accountBalanceMapper)
            ?: throw AccountBalanceNotFoundException(number)
    }

    override fun push(accountBalance: AccountBalance) {
        database
            .bind("id", accountBalance.identity.value)
            .bind("balance", accountBalance.balance.value)
            .bind("updated_at", accountBalance.updatedAt)
            .upsert(UPDATE)
    }
}