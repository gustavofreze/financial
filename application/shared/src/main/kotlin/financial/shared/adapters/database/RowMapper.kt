package financial.shared.adapters.database

/**
 * Translation of one row into the value it holds.
 */
fun interface RowMapper<T> {
    /**
     * Maps the given row to the value it holds.
     */
    fun map(row: Row): T
}
