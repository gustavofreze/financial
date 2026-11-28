package financial.shared.adapters.database

import com.zaxxer.hikari.HikariDataSource
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.JdbiException

class JdbiDatabase(private val jdbi: Jdbi) : Database {

    override fun <T> fetchOne(sql: String, mapper: RowMapper<T>, parameters: Map<String, Any?>): T? = try {
        jdbi.withHandle<T?, Exception> { handle ->
            JdbiSession(handle = handle).fetchOne(sql = sql, mapper = mapper, parameters = parameters)
        }
    } catch (exception: JdbiException) {
        throw DatabaseFailure(cause = exception)
    }

    override fun <T> fetchAll(sql: String, mapper: RowMapper<T>, parameters: Map<String, Any?>): List<T> = try {
        jdbi.withHandle<List<T>, Exception> { handle ->
            JdbiSession(handle = handle).fetchAll(sql = sql, mapper = mapper, parameters = parameters)
        }
    } catch (exception: JdbiException) {
        throw DatabaseFailure(cause = exception)
    }

    override fun update(sql: String, parameters: Map<String, Any?>): Int = try {
        jdbi.withHandle<Int, Exception> { handle ->
            JdbiSession(handle = handle).update(sql = sql, parameters = parameters)
        }
    } catch (exception: JdbiException) {
        throw DatabaseFailure(cause = exception)
    }

    override fun inTransaction(work: UnitOfWork) = try {
        jdbi.useTransaction<Exception> { handle -> work.execute(session = JdbiSession(handle = handle)) }
    } catch (exception: JdbiException) {
        throw DatabaseFailure(cause = exception)
    }

    companion object {
        fun from(settings: DatabaseSettings): JdbiDatabase =
            JdbiDatabase(jdbi = Jdbi.create(HikariDataSource(settings.pool())))
    }
}
