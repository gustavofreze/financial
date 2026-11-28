package financial.accounting.account.application.handlers

import financial.accounting.account.application.commands.AdjustBalance
import financial.accounting.account.application.exceptions.AccountBalanceNotFound
import financial.accounting.account.application.ports.inbound.BalanceAdjusting
import financial.accounting.account.application.ports.outbound.AccountBalances
import java.time.Clock
import java.time.OffsetDateTime

class BalanceAdjustingHandler(private val clock: Clock, private val balances: AccountBalances) : BalanceAdjusting {

    override fun handle(command: AdjustBalance) {
        val at = OffsetDateTime.now(clock)
        val (debit, credit) = listOf(command.debitAccount, command.creditAccount).map { number ->
            balances.find(number = number) ?: throw AccountBalanceNotFound(number = number)
        }

        balances.adjust(
            debited = debit.debit(at = at, amount = command.amount),
            credited = credit.credit(at = at, amount = command.amount),
            transactionId = command.transactionId
        )
    }
}
