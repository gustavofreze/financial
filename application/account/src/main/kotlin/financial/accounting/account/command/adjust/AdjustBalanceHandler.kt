package financial.accounting.account.command.adjust

import financial.accounting.account.core.model.AccountBalanceRepository
import financial.shared.adapters.console.command.resolver.CommandHandler

class AdjustBalanceHandler(
    private val accountBalanceRepository: AccountBalanceRepository
) : CommandHandler<AdjustBalance> {

    override fun handle(command: AdjustBalance) {
        val amount = command.balance
        val debitAccount = accountBalanceRepository.pull(command.debitAccount)
        val creditAccount = accountBalanceRepository.pull(command.creditAccount)

        debitAccount.apply { accountBalanceRepository.push(debit(amount)) }
        creditAccount.apply { accountBalanceRepository.push(credit(amount)) }
    }
}