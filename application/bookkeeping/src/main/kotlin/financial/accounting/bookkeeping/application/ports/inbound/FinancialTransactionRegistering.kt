package financial.accounting.bookkeeping.application.ports.inbound

import financial.accounting.bookkeeping.application.commands.RegisterFinancialTransaction

/**
 * Registration of the financial transactions that move an amount from a debit account to a credit account.
 */
fun interface FinancialTransactionRegistering {
    /**
     * Registers a new transaction with the amount and the accounts of the given order, and appends the fact it raised
     * to the history of the transactions.
     */
    fun handle(command: RegisterFinancialTransaction)
}
