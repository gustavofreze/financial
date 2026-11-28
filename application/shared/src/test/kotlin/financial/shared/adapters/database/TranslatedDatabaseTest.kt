package financial.shared.adapters.database

import financial.shared.testing.H2
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertIs

@Tag("Integration")
class TranslatedDatabaseTest {

    private val mapper = RowMapper { row -> row.text(column = "name") }

    private val database = TranslatedDatabase(
        database = H2.database("CREATE TABLE accounts (id INT PRIMARY KEY, name VARCHAR(20))"),
        translate = { failure -> UnsupportedOperationException(failure) }
    )

    @Test
    fun `fetch one when the statement runs then answers as the database does`() {
        /** @Given an account named cash */
        database.update(sql = "INSERT INTO accounts VALUES (1, 'cash')", parameters = emptyMap())

        /** @When one row is fetched */
        val actual = database.fetchOne(sql = "SELECT name FROM accounts", mapper = mapper, parameters = emptyMap())

        /** @Then the row maps to the cash name */
        assertEquals("cash", actual)
    }

    @Test
    fun `fetch all when the statement runs then answers as the database does`() {
        /** @Given the cash and bank accounts */
        database.update(sql = "INSERT INTO accounts VALUES (1, 'cash'), (2, 'bank')", parameters = emptyMap())

        /** @When every row is fetched in order */
        val actual = database.fetchAll(sql = ORDERED_NAMES, mapper = mapper, parameters = emptyMap())

        /** @Then each row maps to its name in order */
        assertEquals(listOf("cash", "bank"), actual)
    }

    @Test
    fun `update when the statement runs then answers how many rows changed`() {
        /** @Given a statement inserting two accounts */
        val sql = "INSERT INTO accounts VALUES (1, 'cash'), (2, 'bank')"

        /** @When the statement runs */
        val actual = database.update(sql = sql, parameters = emptyMap())

        /** @Then two rows changed */
        assertEquals(2, actual)
    }

    @Test
    fun `in transaction when the statements run then commits them`() {
        /** @Given the cash and bank accounts */
        database.update(sql = "INSERT INTO accounts VALUES (1, 'cash'), (2, 'bank')", parameters = emptyMap())

        /** @When the bank account is renamed inside a transaction */
        database.inTransaction { session ->
            session.update(sql = "UPDATE accounts SET name = 'safe' WHERE id = 2", parameters = emptyMap())
        }

        /** @Then the bank account carries the new name */
        val names = database.fetchAll(sql = ORDERED_NAMES, mapper = mapper, parameters = emptyMap())
        assertEquals(listOf("cash", "safe"), names)
    }

    @Test
    fun `fetch one when the statement fails then raises the translated failure`() {
        /** @When one row is fetched from a table that does not exist */
        val actual = assertThrows<UnsupportedOperationException> {
            database.fetchOne(sql = "SELECT name FROM missing", mapper = mapper, parameters = emptyMap())
        }

        /** @Then the translated failure carries the database failure */
        assertIs<DatabaseFailure>(actual.cause)
    }

    @Test
    fun `fetch all when the statement fails then raises the translated failure`() {
        /** @When every row is fetched from a table that does not exist */
        val actual = assertThrows<UnsupportedOperationException> {
            database.fetchAll(sql = "SELECT name FROM missing", mapper = mapper, parameters = emptyMap())
        }

        /** @Then the translated failure carries the database failure */
        assertIs<DatabaseFailure>(actual.cause)
    }

    @Test
    fun `update when the statement fails then raises the translated failure`() {
        /** @When rows are deleted from a table that does not exist */
        val actual = assertThrows<UnsupportedOperationException> {
            database.update(sql = "DELETE FROM missing", parameters = emptyMap())
        }

        /** @Then the translated failure carries the database failure */
        assertIs<DatabaseFailure>(actual.cause)
    }

    @Test
    fun `in transaction when a statement fails then raises the translated failure`() {
        /** @When a transaction deletes rows from a table that does not exist */
        val actual = assertThrows<UnsupportedOperationException> {
            database.inTransaction { session -> session.update(sql = "DELETE FROM missing", parameters = emptyMap()) }
        }

        /** @Then the translated failure carries the database failure */
        assertIs<DatabaseFailure>(actual.cause)
    }

    @Test
    fun `in transaction when the block fails on its own then keeps that failure`() {
        /** @When the transaction block raises a failure of its own */
        val actual = assertThrows<IllegalArgumentException> {
            database.inTransaction { throw IllegalArgumentException() }
        }

        /** @Then the failure keeps its own type */
        assertEquals(IllegalArgumentException::class, actual::class)
    }

    private companion object {
        const val ORDERED_NAMES = "SELECT name FROM accounts ORDER BY id"
    }
}
