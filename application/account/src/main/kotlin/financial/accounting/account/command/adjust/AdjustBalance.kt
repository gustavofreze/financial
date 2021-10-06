package financial.accounting.account.command.adjust

import financial.shared.adapters.console.command.Command
import financial.shared.valueobject.Amount

data class AdjustBalance(
    val id: String,
    val balance: Amount,
    val debitAccount: String,
    val creditAccount: String
) : Command