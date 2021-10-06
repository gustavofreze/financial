package financial.accounting.bookkeeping.command.register

import financial.shared.adapters.console.command.Command
import financial.shared.valueobject.Amount
import financial.shared.valueobject.Currency

class RegisterFinancialTransaction : Command {

    val amount = Amount(value = (10..10000).random().toBigDecimal(), currency = Currency.BRL)
    val debitAccount = "2.1.1.001"
    val creditAccount = "2.2.1.001"
}