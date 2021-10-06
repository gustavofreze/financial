package financial.shared.databases

interface RelationalDatabase : Database {

    fun bind(name: String, value: Any): RelationalDatabase

    fun query(sql: String): RelationalDatabase

    fun upsert(sql: String)

    fun execute(sql: String)

    fun <T> fetchOne(mapper: DatabaseMapper<T>): T?

    fun <T> fetchAll(mapper: DatabaseMapper<T>): List<T>
}