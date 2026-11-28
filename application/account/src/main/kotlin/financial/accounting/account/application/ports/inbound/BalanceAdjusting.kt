package financial.accounting.account.application.ports.inbound

import financial.accounting.account.application.commands.AdjustBalance

/**
 * Adjustment of the balances of the two accounts a financial transaction moves an amount between.
 */
fun interface BalanceAdjusting {
    /**
     * Debits the amount of the given order from its debit account and credits it to its credit account, once per
     * transaction id.
     *
     * Fails with AccountBalanceNotFound when either account does not exist, and with CurrencyMismatch when the amount
     * is in another currency than a balance.
     */
    fun handle(command: AdjustBalance)
}
