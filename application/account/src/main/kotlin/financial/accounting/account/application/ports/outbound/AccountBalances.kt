package financial.accounting.account.application.ports.outbound

import financial.accounting.account.application.domain.models.AccountBalance
import financial.accounting.account.application.domain.models.AccountNumber
import financial.accounting.account.application.domain.models.TransactionId

/**
 * Balances of the ledger accounts, one per account number.
 *
 * Fails with AccountBalancesUnavailable, carrying the cause, when the balances cannot be read or written.
 */
interface AccountBalances {
    /**
     * Finds the balance of the account with the given number, or null when no such account exists.
     */
    fun find(number: AccountNumber): AccountBalance?

    /**
     * Adjusts the debited and the credited balances together, once per given transaction id.
     *
     * A transaction id already adjusted leaves both balances as they are.
     *
     * Fails with BalanceAdjustmentConflict, carrying the account number, when either balance changed after it was
     * found, and then adjusts neither balance and keeps the transaction id unadjusted.
     */
    fun adjust(debited: AccountBalance, credited: AccountBalance, transactionId: TransactionId)
}
