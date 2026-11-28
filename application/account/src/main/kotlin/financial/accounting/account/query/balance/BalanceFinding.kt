package financial.accounting.account.query.balance

/**
 * Finding of the balances of the ledger accounts, apart from the write side.
 *
 * Fails with BalanceFindingUnavailable, carrying the cause, when the balances cannot be read.
 */
fun interface BalanceFinding {
    /**
     * Finds the balance of every account, in the order the accounts were opened.
     */
    fun findAll(): List<Balance>
}
