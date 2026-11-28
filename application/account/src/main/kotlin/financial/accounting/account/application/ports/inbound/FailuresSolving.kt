package financial.accounting.account.application.ports.inbound

import financial.accounting.account.application.commands.SolveFailures

/**
 * Replaying of the failed reactions kept in the troubleshooting queue.
 */
fun interface FailuresSolving {
    /**
     * Replays the oldest pending failures, up to the limit of the given order, resolving each replay that succeeds and
     * retaining each one that fails with the cause of its new failure.
     */
    fun handle(command: SolveFailures)
}
