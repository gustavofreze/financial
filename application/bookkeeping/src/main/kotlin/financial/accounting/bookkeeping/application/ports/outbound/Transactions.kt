package financial.accounting.bookkeeping.application.ports.outbound

import financial.accounting.bookkeeping.application.domain.models.Transaction

/**
 * History of the financial transactions, kept as the facts each transaction raised.
 *
 * Fails with TransactionsUnavailable, carrying the cause, when the history cannot be written.
 */
fun interface Transactions {
    /**
     * Appends the facts the given transaction raised to its history, all of them or none.
     */
    fun append(transaction: Transaction)
}
