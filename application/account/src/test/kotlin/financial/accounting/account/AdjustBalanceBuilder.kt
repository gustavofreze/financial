package financial.accounting.account

import financial.accounting.account.application.commands.AdjustBalance
import financial.accounting.account.application.domain.models.AccountNumber
import financial.accounting.account.application.domain.models.TransactionId
import financial.shared.domain.models.Amount
import financial.shared.testing.Amounts

class AdjustBalanceBuilder private constructor(
    private val amount: Amount,
    private val debitAccount: AccountNumber,
    private val creditAccount: AccountNumber,
    private val transactionId: TransactionId
) {

    fun build(): AdjustBalance = AdjustBalance(
        amount = amount,
        debitAccount = debitAccount,
        creditAccount = creditAccount,
        transactionId = transactionId
    )

    companion object {
        fun fromDebitToCredit(): AdjustBalanceBuilder = AdjustBalanceBuilder(
            amount = Amounts.brl(value = "30"),
            debitAccount = AccountNumber(value = "2.1.1.001"),
            creditAccount = AccountNumber(value = "2.2.1.001"),
            transactionId = TransactionId(value = "t1")
        )
    }
}
