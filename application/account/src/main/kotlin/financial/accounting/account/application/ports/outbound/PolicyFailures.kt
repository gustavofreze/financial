package financial.accounting.account.application.ports.outbound

/**
 * Troubleshooting queue of the reactions that failed, kept until a replay of each one succeeds.
 *
 * Fails with PolicyFailuresUnavailable, carrying the cause, when the queue cannot be read or written.
 */
interface PolicyFailures {
    /**
     * Records the given failure as pending, with the payload of the fact, the policy that failed on it and its cause.
     */
    fun record(failure: PolicyFailure)

    /**
     * Returns at most the given limit of pending failures, oldest first.
     */
    fun pending(limit: Int): List<PendingPolicyFailure>

    /**
     * Resolves the pending failure with the given id, so no later replay picks it again.
     */
    fun resolve(id: String)

    /**
     * Retains the pending failure with the given id, replacing its cause with the one of the last replay.
     */
    fun retain(id: String, cause: Exception)
}
