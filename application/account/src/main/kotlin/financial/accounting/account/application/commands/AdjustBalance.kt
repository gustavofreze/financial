package financial.accounting.account.application.commands

import financial.accounting.account.application.domain.models.AccountNumber
import financial.accounting.account.application.domain.models.TransactionId
import financial.shared.domain.models.Amount

data class AdjustBalance(
    val amount: Amount,
    val debitAccount: AccountNumber,
    val creditAccount: AccountNumber,
    val transactionId: TransactionId
)
