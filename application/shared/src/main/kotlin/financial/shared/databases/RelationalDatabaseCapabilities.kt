package financial.shared.databases

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.statement.Query
import org.jdbi.v3.core.statement.SqlStatements

class RelationalDatabaseCapabilities(private val jdbi: Jdbi) : RelationalDatabase {

    private lateinit var query: Query

    private val bindMap = mutableMapOf<String, Any>()

    init {
        jdbi.getConfig(SqlStatements::class.java).isUnusedBindingAllowed = true
    }

    override fun bind(name: String, value: Any): RelationalDatabase {
        bindMap[name] = value
        return this
    }

    override fun query(sql: String): RelationalDatabase {
        val handle: Handle = jdbi.open()
        query = handle.createQuery(sql).bindMap(bindMap)
        handle.close()
        return this
    }

    override fun upsert(sql: String) {
        jdbi.useHandle<Exception> { handle ->
            handle.useTransaction<Exception> { init ->
                init.createUpdate(sql)
                    .bindMap(bindMap)
                    .execute()
            }
        }
    }

    override fun execute(sql: String) {
        val handle: Handle = jdbi.open()
        handle.execute(sql)
        handle.close()
    }

    override fun <T> fetchOne(mapper: DatabaseMapper<T>): T? {
        return query
            .map(mapper)
            .findFirst()
            .orElse(null)
            .also { clear() }
    }

    override fun <T> fetchAll(mapper: DatabaseMapper<T>): List<T> {
        return query
            .map(mapper)
            .list()
            .orEmpty()
            .also { clear() }
    }

    private fun clear() {
        bindMap.clear()
    }
}