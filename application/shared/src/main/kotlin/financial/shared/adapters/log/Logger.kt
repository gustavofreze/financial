package financial.shared.adapters.log

/**
 * Log of what the application did, read by an operator.
 */
fun interface Logger {
    /**
     * Logs the given message as information about a step that went as expected.
     */
    fun info(message: String)
}
