package financial.shared.adapters.database

import financial.shared.testing.FixedClock
import financial.shared.testing.H2
import org.jdbi.v3.core.JdbiException
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

@Tag("Integration")
class JdbiDatabaseTest {

    private val mapper = RowMapper { row -> row.text(column = "name") }

    private val database = H2.database(
        "CREATE TABLE accounts (id INT PRIMARY KEY, name VARCHAR(20))",
        "INSERT INTO accounts VALUES (1, 'cash'), (2, 'bank')"
    )

    @Test
    fun `fetch one when a row matches then maps it`() {
        /** @Given the id of the bank account */
        val parameters = mapOf("id" to 2)

        /** @When one row is fetched by that id */
        val actual = database.fetchOne(sql = FIND_BY_ID, mapper = mapper, parameters = parameters)

        /** @Then the row maps to the bank name */
        assertEquals("bank", actual)
    }

    @Test
    fun `fetch one when no row matches then is null`() {
        /** @Given an id no account holds */
        val parameters = mapOf("id" to 9)

        /** @When one row is fetched by that id */
        val actual = database.fetchOne(sql = FIND_BY_ID, mapper = mapper, parameters = parameters)

        /** @Then no row is answered */
        assertNull(actual)
    }

    @Test
    fun `fetch all when rows exist then maps every row`() {
        /** @Given a query of every account name ordered by id */
        val sql = "SELECT name FROM accounts ORDER BY id"

        /** @When every row is fetched */
        val actual = database.fetchAll(sql = sql, mapper = mapper, parameters = emptyMap())

        /** @Then each row maps to its name in order */
        assertEquals(listOf("cash", "bank"), actual)
    }

    @Test
    fun `update when a row matches then returns the affected count`() {
        /** @Given a new name for the cash account */
        val parameters = mapOf("name" to "safe", "id" to 1)

        /** @When the account name is updated */
        val actual = database.update(sql = "UPDATE accounts SET name = :name WHERE id = :id", parameters = parameters)

        /** @Then one row is affected */
        assertEquals(1, actual)
    }

    @Test
    fun `in transaction when the block fails then rolls every statement back`() {
        /** @Given a failure the transaction block raises */
        val failure = IllegalStateException("abort")

        /** @When a transaction renames the cash account and then raises that failure */
        assertThrows<IllegalStateException> {
            database.inTransaction { session ->
                session.update(sql = "UPDATE accounts SET name = 'lost' WHERE id = 1", parameters = emptyMap())
                throw failure
            }
        }

        /** @Then the cash account keeps its name */
        assertEquals("cash", nameOf(id = 1))
    }

    @Test
    fun `in transaction when the block succeeds then commits every statement`() {
        /** @Given a statement renaming the cash account */
        val sql = "UPDATE accounts SET name = 'kept' WHERE id = 1"

        /** @When the statement runs inside a transaction */
        database.inTransaction { session -> session.update(sql = sql, parameters = emptyMap()) }

        /** @Then the cash account carries the new name */
        assertEquals("kept", nameOf(id = 1))
    }

    @Test
    fun `in transaction when nested then reuses the same session`() {
        /** @Given a statement renaming the bank account */
        val sql = "UPDATE accounts SET name = 'nested' WHERE id = 2"

        /** @When the statement runs in a transaction nested inside another */
        database.inTransaction { session ->
            session.inTransaction { inner -> inner.update(sql = sql, parameters = emptyMap()) }
        }

        /** @Then the bank account carries the new name */
        assertEquals("nested", nameOf(id = 2))
    }

    @Test
    fun `fetch all when the statement cannot run then fails with the database failure carrying the cause`() {
        /** @Given a query on a table that does not exist */
        val sql = "SELECT name FROM missing"

        /** @When every row is fetched */
        val actual = assertThrows<DatabaseFailure> {
            database.fetchAll(sql = sql, mapper = mapper, parameters = emptyMap())
        }

        /** @Then the database failure carries the jdbi cause */
        assertIs<JdbiException>(actual.cause)
    }

    @Test
    fun `in transaction when a statement cannot run then fails with the database failure`() {
        /** @Given an update on a table that does not exist */
        val sql = "UPDATE missing SET name = 'lost'"

        /** @When the update runs inside a transaction */
        val actual = assertThrows<DatabaseFailure> {
            database.inTransaction { session -> session.update(sql = sql, parameters = emptyMap()) }
        }

        /** @Then the database failure carries the jdbi cause */
        assertIs<JdbiException>(actual.cause)
    }

    @Test
    fun `fetch one when columns of every type are read then maps each one`() {
        /** @Given a query selecting a whole number, a decimal and a moment */
        val sql = "SELECT 7 AS whole, CAST(1.50 AS NUMERIC(3, 2)) AS decimal, " +
            "TIMESTAMP WITH TIME ZONE '2026-01-15 10:30:00+00' AS moment"

        /** @And a mapper reading each column by its type */
        val mapper = RowMapper { row ->
            Triple(
                row.integer(column = "whole"),
                row.decimal(column = "decimal"),
                row.dateTime(column = "moment").toInstant()
            )
        }

        /** @When one row is fetched */
        val actual = database.fetchOne(sql = sql, mapper = mapper, parameters = emptyMap())

        /** @Then each column maps to its value */
        assertEquals(Triple(7, BigDecimal("1.50"), FixedClock.instant), actual)
    }

    @Test
    fun `fetch one when a whole number exceeds the integer range then reads it as a long`() {
        /** @Given a query selecting a whole number beyond the integer range */
        val sql = "SELECT CAST(3000000000 AS BIGINT) AS large"

        /** @And a mapper reading it as a long */
        val mapper = RowMapper { row -> row.long(column = "large") }

        /** @When one row is fetched */
        val actual = database.fetchOne(sql = sql, mapper = mapper, parameters = emptyMap())

        /** @Then the whole number is read as a long */
        assertEquals(3000000000, actual)
    }

    @ParameterizedTest
    @MethodSource("nullColumns")
    fun `fetch one when a column holds null then fails with the column name`(sql: String, mapper: RowMapper<Any>) {
        /** @When a row holding a null column is fetched */
        val actual = assertThrows<NullColumn> {
            database.fetchOne(sql = sql, mapper = mapper, parameters = emptyMap())
        }

        /** @Then the failure names the null column */
        assertEquals("empty", actual.column)
    }

    @Test
    fun `from when the settings point at a database then runs statements through the pool`() {
        /** @Given settings pointing at an in-memory database */
        val settings = DatabaseSettings(user = "sa", jdbcUrl = "jdbc:h2:mem:${UUID.randomUUID()}", password = "")

        /** @When a statement runs through the pool built from those settings */
        val actual = JdbiDatabase.from(settings = settings)
            .fetchOne(sql = "SELECT 1 AS one", mapper = { row -> row.integer(column = "one") }, parameters = emptyMap())

        /** @Then the statement answers its value */
        assertEquals(1, actual)
    }

    private fun nameOf(id: Int): String? =
        database.fetchOne(sql = FIND_BY_ID, mapper = mapper, parameters = mapOf("id" to id))

    private companion object {
        const val FIND_BY_ID = "SELECT name FROM accounts WHERE id = :id"

        @JvmStatic
        fun nullColumns() = listOf(
            Arguments.of("SELECT CAST(NULL AS VARCHAR(20)) AS empty", RowMapper { row -> row.text(column = "empty") }),
            Arguments.of("SELECT CAST(NULL AS BIGINT) AS empty", RowMapper { row -> row.long(column = "empty") }),
            Arguments.of("SELECT CAST(NULL AS INT) AS empty", RowMapper { row -> row.integer(column = "empty") }),
            Arguments.of(
                "SELECT CAST(NULL AS NUMERIC(3, 2)) AS empty",
                RowMapper { row -> row.decimal(column = "empty") }
            ),
            Arguments.of(
                "SELECT CAST(NULL AS TIMESTAMP WITH TIME ZONE) AS empty",
                RowMapper { row -> row.dateTime(column = "empty") }
            )
        )
    }
}
