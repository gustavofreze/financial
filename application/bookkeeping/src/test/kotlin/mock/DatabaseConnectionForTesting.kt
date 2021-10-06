package mock

import financial.shared.databases.DatabaseSettings
import org.jdbi.v3.core.Jdbi
import java.sql.DriverManager

class DatabaseConnectionForTesting(private val settings: DatabaseSettings) {

    val jdbi: Jdbi by lazy {
        val connection = DriverManager.getConnection(settings.jdbcUrl, settings.user, settings.password)
        val jdbi = Jdbi.create(connection)
        jdbi.installPlugins()
        jdbi
    }
}