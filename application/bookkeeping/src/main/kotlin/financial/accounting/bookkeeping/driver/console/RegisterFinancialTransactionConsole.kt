package financial.accounting.bookkeeping.driver.console

import financial.accounting.bookkeeping.application.commands.RegisterFinancialTransaction
import financial.accounting.bookkeeping.application.domain.models.AccountNumber
import financial.accounting.bookkeeping.application.ports.inbound.FinancialTransactionRegistering
import financial.shared.adapters.console.ConsoleCommand
import financial.shared.domain.models.Amount
import financial.shared.domain.models.Currency
import kotlin.random.Random
import kotlin.random.nextInt

class RegisterFinancialTransactionConsole(
    private val random: Random = Random.Default,
    private val registering: FinancialTransactionRegistering
) : ConsoleCommand {

    override val name = "RegisterFinancialTransaction"

    override fun run() {
        val value = random.nextInt(MINIMUM_AMOUNT..MAXIMUM_AMOUNT).toBigDecimal()
        val command = RegisterFinancialTransaction(
            amount = Amount(value = value, currency = Currency.BRL),
            debitAccount = AccountNumber(value = "2.1.1.001"),
            creditAccount = AccountNumber(value = "2.2.1.001")
        )

        registering.handle(command = command)
    }

    private companion object {
        const val MINIMUM_AMOUNT = 10
        const val MAXIMUM_AMOUNT = 10000
    }
}
