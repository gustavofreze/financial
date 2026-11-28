package financial.shared.adapters.database

class TranslatedDatabase(private val database: Database, private val translate: (DatabaseFailure) -> Exception) :
    Database {

    override fun <T> fetchOne(sql: String, mapper: RowMapper<T>, parameters: Map<String, Any?>): T? = try {
        database.fetchOne(sql = sql, mapper = mapper, parameters = parameters)
    } catch (failure: DatabaseFailure) {
        throw translate(failure)
    }

    override fun <T> fetchAll(sql: String, mapper: RowMapper<T>, parameters: Map<String, Any?>): List<T> = try {
        database.fetchAll(sql = sql, mapper = mapper, parameters = parameters)
    } catch (failure: DatabaseFailure) {
        throw translate(failure)
    }

    override fun update(sql: String, parameters: Map<String, Any?>): Int = try {
        database.update(sql = sql, parameters = parameters)
    } catch (failure: DatabaseFailure) {
        throw translate(failure)
    }

    override fun inTransaction(work: UnitOfWork) = try {
        database.inTransaction(work = work)
    } catch (failure: DatabaseFailure) {
        throw translate(failure)
    }
}
