package financial.shared.adapters.console

/**
 * Use case an operator runs by name from the console.
 */
interface ConsoleCommand {
    /**
     * The name the operator types to run the use case.
     */
    val name: String

    /**
     * Runs the use case once.
     */
    fun run()
}
