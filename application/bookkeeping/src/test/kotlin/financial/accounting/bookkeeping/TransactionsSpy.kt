package financial.accounting.bookkeeping

import financial.accounting.bookkeeping.application.domain.models.Transaction
import financial.accounting.bookkeeping.application.ports.outbound.Transactions

class TransactionsSpy : Transactions {

    val appended = mutableListOf<Transaction>()

    override fun append(transaction: Transaction) {
        appended.add(transaction)
    }
}
