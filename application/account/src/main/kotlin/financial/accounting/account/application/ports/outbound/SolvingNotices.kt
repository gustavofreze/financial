package financial.accounting.account.application.ports.outbound

/**
 * Notices about the replays of the troubleshooting queue, read by an operator.
 */
fun interface SolvingNotices {
    /**
     * Notifies that the given count of pending failures went through a replay.
     */
    fun solved(count: Int)
}
