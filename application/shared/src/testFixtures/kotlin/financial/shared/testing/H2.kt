package financial.shared.testing

import financial.shared.adapters.database.Database
import financial.shared.adapters.database.JdbiDatabase
import org.jdbi.v3.core.Jdbi
import java.util.UUID

object H2 {

    fun database(vararg statements: String): Database {
        val jdbi = Jdbi.create("jdbc:h2:mem:${UUID.randomUUID()};DB_CLOSE_DELAY=-1;MODE=PostgreSQL", "test", "test")
        jdbi.useHandle<Exception> { handle -> statements.forEach(handle::execute) }
        return JdbiDatabase(jdbi = jdbi)
    }
}
