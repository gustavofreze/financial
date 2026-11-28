package financial.shared.adapters.database

/**
 * Relational database the adapters read from and write to with SQL and named parameters.
 *
 * Fails with DatabaseFailure, carrying the cause, when the database cannot run a statement.
 */
interface Database {
    /**
     * Fetches the first row the given SQL selects with the given parameters, mapped by the given mapper, or null when
     * no row matches.
     */
    fun <T> fetchOne(sql: String, mapper: RowMapper<T>, parameters: Map<String, Any?>): T?

    /**
     * Fetches every row the given SQL selects with the given parameters, each mapped by the given mapper.
     */
    fun <T> fetchAll(sql: String, mapper: RowMapper<T>, parameters: Map<String, Any?>): List<T>

    /**
     * Updates the rows the given SQL changes with the given parameters, and returns how many changed.
     */
    fun update(sql: String, parameters: Map<String, Any?>): Int

    /**
     * Runs the given work against one transaction, committed when the work returns and rolled back when it fails.
     */
    fun inTransaction(work: UnitOfWork)
}
