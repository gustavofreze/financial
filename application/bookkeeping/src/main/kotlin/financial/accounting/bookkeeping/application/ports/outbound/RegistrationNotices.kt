package financial.accounting.bookkeeping.application.ports.outbound

import financial.accounting.bookkeeping.application.domain.models.Transaction

/**
 * Notices about the registered financial transactions, read by an operator.
 */
fun interface RegistrationNotices {
    /**
     * Notifies that the given transaction has been registered.
     */
    fun registered(transaction: Transaction)
}
