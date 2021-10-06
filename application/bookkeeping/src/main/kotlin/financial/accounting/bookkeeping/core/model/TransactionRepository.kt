package financial.accounting.bookkeeping.core.model

interface TransactionRepository {

    fun push(transaction: Transaction)
}