package financial.shared.adapters.database

import org.jdbi.v3.core.Handle

internal class JdbiSession(private val handle: Handle) : Database {

    override fun <T> fetchOne(sql: String, mapper: RowMapper<T>, parameters: Map<String, Any?>): T? =
        handle.createQuery(sql)
            .bindMap(parameters)
            .map { resultSet, _ -> mapper.map(row = ResultSetRow(resultSet = resultSet)) }
            .findFirst()
            .orElse(null)

    override fun <T> fetchAll(sql: String, mapper: RowMapper<T>, parameters: Map<String, Any?>): List<T> =
        handle.createQuery(sql)
            .bindMap(parameters)
            .map { resultSet, _ -> mapper.map(row = ResultSetRow(resultSet = resultSet)) }
            .list()

    override fun update(sql: String, parameters: Map<String, Any?>): Int =
        handle.createUpdate(sql).bindMap(parameters).execute()

    override fun inTransaction(work: UnitOfWork) = work.execute(session = this)
}
