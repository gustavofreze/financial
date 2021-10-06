package mock

import financial.shared.Environment
import financial.shared.databases.DatabaseSettings

class DatabaseSettingsForTesting : DatabaseSettings {

    override val user = Environment.get("TEST_DATABASE_USER")
    override val jdbcUrl = Environment.get("TEST_DATABASE_JDBC_URL")
    override val password = Environment.get("TEST_DATABASE_PASSWORD")
}