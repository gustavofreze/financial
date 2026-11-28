package financial.accounting.bookkeeping.application.commands

import financial.accounting.bookkeeping.application.domain.models.AccountNumber
import financial.shared.domain.models.Amount

data class RegisterFinancialTransaction(
    val amount: Amount,
    val debitAccount: AccountNumber,
    val creditAccount: AccountNumber
)
