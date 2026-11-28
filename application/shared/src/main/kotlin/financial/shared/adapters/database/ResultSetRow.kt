package financial.shared.adapters.database

import java.math.BigDecimal
import java.sql.ResultSet
import java.time.OffsetDateTime

internal class ResultSetRow(private val resultSet: ResultSet) : Row {

    override fun text(column: String): String = resultSet.getString(column) ?: throw NullColumn(column = column)

    override fun long(column: String): Long =
        resultSet.getLong(column).takeUnless { resultSet.wasNull() } ?: throw NullColumn(column = column)

    override fun integer(column: String): Int =
        resultSet.getInt(column).takeUnless { resultSet.wasNull() } ?: throw NullColumn(column = column)

    override fun decimal(column: String): BigDecimal =
        resultSet.getBigDecimal(column) ?: throw NullColumn(column = column)

    override fun dateTime(column: String): OffsetDateTime =
        resultSet.getObject(column, OffsetDateTime::class.java) ?: throw NullColumn(column = column)
}
