package financial.accounting.account.application.ports.outbound

/**
 * Replay of one policy against the payload of a fact it failed to react to.
 */
interface PolicyReplay {
    /**
     * The name the troubleshooting queue records the policy under.
     */
    val name: String

    /**
     * Reacts again to the fact the given payload carries.
     */
    fun replay(payload: String)
}
