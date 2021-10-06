package financial.shared.databases

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

typealias DatabaseContext = StatementContext

interface DatabaseMapper<T> : RowMapper<T> {

    override fun map(resultSet: ResultSet, context: DatabaseContext): T = map(resultSet, context)
}