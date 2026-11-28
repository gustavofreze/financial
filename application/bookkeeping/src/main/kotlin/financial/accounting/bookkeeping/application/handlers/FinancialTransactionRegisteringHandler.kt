package financial.accounting.bookkeeping.application.handlers

import financial.accounting.bookkeeping.application.commands.RegisterFinancialTransaction
import financial.accounting.bookkeeping.application.domain.models.Transaction
import financial.accounting.bookkeeping.application.domain.models.TransactionId
import financial.accounting.bookkeeping.application.ports.inbound.FinancialTransactionRegistering
import financial.accounting.bookkeeping.application.ports.outbound.RegistrationNotices
import financial.accounting.bookkeeping.application.ports.outbound.Transactions
import java.time.Clock
import java.time.OffsetDateTime

class FinancialTransactionRegisteringHandler(
    private val clock: Clock,
    private val notices: RegistrationNotices,
    private val transactions: Transactions
) : FinancialTransactionRegistering {

    override fun handle(command: RegisterFinancialTransaction) {
        val transaction = Transaction.register(
            id = TransactionId.generate(),
            amount = command.amount,
            occurredOn = OffsetDateTime.now(clock),
            debitAccount = command.debitAccount,
            creditAccount = command.creditAccount
        )

        transactions.append(transaction = transaction)
        notices.registered(transaction = transaction)
    }
}
