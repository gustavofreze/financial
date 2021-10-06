package financial.accounting.bookkeeping.command.register

import financial.accounting.bookkeeping.core.model.Transaction
import financial.accounting.bookkeeping.core.valueobject.TransactionId
import financial.accounting.bookkeeping.core.model.TransactionRepository
import financial.shared.adapters.console.command.resolver.CommandHandler
import financial.shared.eventsourcing.EventSourcing
import financial.shared.log.Logger
import financial.shared.valueobject.Version

class RegisterFinancialTransactionHandler(
    private val logger: Logger,
    private val eventSourcing: EventSourcing<Transaction>,
    private val bookkeepingRepository: TransactionRepository
) : CommandHandler<RegisterFinancialTransaction> {

    override fun handle(command: RegisterFinancialTransaction) {
        val transaction = Transaction(
            identity = TransactionId.generate(),
            sequence = Version(0),
            eventSourcing = eventSourcing
        )

        transaction.register(
            amount = command.amount,
            debitAccount = command.debitAccount,
            creditAccount = command.creditAccount
        )

        bookkeepingRepository.push(transaction)
        logger.loggerFor(this::class).info("Transaction with value ${transaction.amount.value} has been registered")
    }
}