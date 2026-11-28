package financial.accounting.bookkeeping.driven.log

import financial.accounting.bookkeeping.application.domain.models.Transaction
import financial.accounting.bookkeeping.application.ports.outbound.RegistrationNotices
import financial.shared.adapters.log.Logger

class LoggedRegistrationNotices(private val logger: Logger) : RegistrationNotices {

    override fun registered(transaction: Transaction) =
        logger.info(message = "Transaction with value ${transaction.amount.value} has been registered")
}
