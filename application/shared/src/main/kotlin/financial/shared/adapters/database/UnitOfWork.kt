package financial.shared.adapters.database

/**
 * Work run against one transaction of the database.
 */
fun interface UnitOfWork {
    /**
     * Executes the work with the given session, which runs every statement inside the transaction.
     */
    fun execute(session: Database)
}
