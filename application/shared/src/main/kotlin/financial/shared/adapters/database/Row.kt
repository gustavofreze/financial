package financial.shared.adapters.database

import java.math.BigDecimal
import java.time.OffsetDateTime

/**
 * One row a query returned, read by column name.
 *
 * Fails with NullColumn, carrying the column name, when the given column holds no value.
 */
interface Row {
    /**
     * Reads the given column as text.
     */
    fun text(column: String): String

    /**
     * Reads the given column as a whole number that may exceed the range of an integer.
     */
    fun long(column: String): Long

    /**
     * Reads the given column as a whole number.
     */
    fun integer(column: String): Int

    /**
     * Reads the given column as a decimal number, keeping its scale.
     */
    fun decimal(column: String): BigDecimal

    /**
     * Reads the given column as a date and time with its offset.
     */
    fun dateTime(column: String): OffsetDateTime
}
