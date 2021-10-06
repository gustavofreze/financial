package financial.accounting.account.driver.policies

import financial.accounting.account.command.adjust.AdjustBalance
import financial.accounting.account.driver.policies.events.TransactionRegistered
import financial.shared.adapters.console.command.bus.CommandBus
import financial.shared.policies.Failures
import financial.shared.policies.Policy

class AdjustBalancePolicy(
    override val failures: Failures,
    private val commandBus: CommandBus<AdjustBalance>
) : Policy<TransactionRegistered>() {

    override val policyClass = AdjustBalancePolicy::class
    override val subscribedEvent = TransactionRegistered::class

    override fun doHandle(event: TransactionRegistered) {
        val command = AdjustBalance(
            id = event.id,
            balance = event.amount,
            debitAccount = event.debitAccount,
            creditAccount = event.creditAccount
        )

        commandBus.dispatch(command)
    }
}