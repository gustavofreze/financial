package financial.shared.application

/**
 * Dispatching of the facts received from another bounded context to the policies that react to them.
 */
fun interface EventBus {
    /**
     * Dispatches the given fact to every policy, each one reacting only to the fact it owns a copy of.
     */
    fun dispatch(event: ReceivedEvent)
}
