package financial.accounting.bookkeeping

import financial.accounting.bookkeeping.application.domain.models.AccountNumber
import financial.accounting.bookkeeping.application.domain.models.Transaction
import financial.accounting.bookkeeping.application.domain.models.TransactionId
import financial.shared.domain.models.Amount
import financial.shared.testing.Amounts
import financial.shared.testing.FixedClock

class TransactionBuilder private constructor(private val id: TransactionId, private val amount: Amount) {

    fun build(): Transaction = Transaction.register(
        id = id,
        amount = amount,
        occurredOn = FixedClock.now,
        debitAccount = AccountNumber(value = "2.1.1.001"),
        creditAccount = AccountNumber(value = "2.2.1.001")
    )

    companion object {
        fun registered(): TransactionBuilder = TransactionBuilder(
            id = TransactionId(value = "4fc06d14-0a42-41af-8d78-710b2a60947d"),
            amount = Amounts.brl(value = "2587")
        )
    }
}
