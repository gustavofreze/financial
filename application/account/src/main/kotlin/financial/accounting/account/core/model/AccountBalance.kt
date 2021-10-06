package financial.accounting.account.core.model

import financial.shared.buildingblocks.aggregate.AggregateRoot
import financial.shared.buildingblocks.entity.Identity
import financial.shared.valueobject.Amount
import financial.shared.valueobject.Version
import java.time.LocalDateTime

class AccountBalance(
    override val identity: Identity,
    val number: String,
    val balance: Amount,
    val updatedAt: LocalDateTime
) : AggregateRoot<AccountBalance> {

    override val modelVersion = Version.first()

    fun debit(amount: Amount): AccountBalance {
        val balance = balance - amount

        return AccountBalance(
            identity = identity,
            number = number,
            balance = balance,
            updatedAt = LocalDateTime.now()
        )
    }

    fun credit(amount: Amount): AccountBalance {
        val balance = balance + amount

        return AccountBalance(
            identity = identity,
            number = number,
            balance = balance,
            updatedAt = LocalDateTime.now()
        )
    }
}