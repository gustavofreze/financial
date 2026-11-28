package financial.accounting.account.driven.repository

import financial.accounting.account.application.domain.models.AccountBalance
import financial.accounting.account.application.domain.models.AccountNumber
import financial.accounting.account.application.domain.models.TransactionId
import financial.accounting.account.application.exceptions.AccountBalancesUnavailable
import financial.accounting.account.application.exceptions.BalanceAdjustmentConflict
import financial.accounting.account.application.ports.outbound.AccountBalances
import financial.shared.adapters.database.Database
import financial.shared.adapters.database.TranslatedDatabase

class DatabaseAccountBalances(database: Database) : AccountBalances {

    private val mapper = AccountBalanceMapper()

    private val database = TranslatedDatabase(
        database = database,
        translate = { failure -> AccountBalancesUnavailable(cause = failure) }
    )

    override fun find(number: AccountNumber): AccountBalance? =
        database.fetchOne(sql = Queries.FIND, mapper = mapper, parameters = mapOf("number" to number.value))

    override fun adjust(debited: AccountBalance, credited: AccountBalance, transactionId: TransactionId) {
        database.inTransaction { session ->
            val adjustment = mapOf("transaction_id" to transactionId.value, "adjusted_at" to credited.updatedAt)

            if (session.update(sql = Queries.REGISTER_ADJUSTMENT, parameters = adjustment) == 0) {
                return@inTransaction
            }

            listOf(debited, credited).forEach { balance ->
                val parameters = mapOf(
                    "id" to balance.id.value,
                    "balance" to balance.balance.value,
                    "version" to balance.version.value,
                    "updated_at" to balance.updatedAt
                )

                if (session.update(sql = Queries.UPDATE, parameters = parameters) == 0) {
                    throw BalanceAdjustmentConflict(number = balance.number)
                }
            }
        }
    }
}
