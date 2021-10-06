package financial.accounting.bookkeeping.core.events

import financial.shared.valueobject.Amount

data class TransactionRegistered(
    val id: String,
    val amount: Amount,
    val status: Int,
    val debitAccount: String,
    val creditAccount: String
) : Event {

    override fun type(): String = this::class.java.simpleName

    override fun revision(): Int = 1
}