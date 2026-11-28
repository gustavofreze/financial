package financial.shared.adapters.kafka

/**
 * Hooks the process runs when it stops.
 */
fun interface ShutdownHooks {
    /**
     * Registers the given hook, run once when the process stops.
     */
    fun register(hook: Thread)
}
