package financial.accounting.account.driven.troubleshoot

import financial.accounting.account.application.ports.outbound.SolvingNotices
import financial.shared.adapters.log.Logger

class LoggedSolvingNotices(private val logger: Logger) : SolvingNotices {

    override fun solved(count: Int) = logger.info(message = "Number of processed failures: $count")
}
