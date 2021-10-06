package financial.shared.databases.postgresql

import financial.shared.Environment
import financial.shared.databases.DatabaseSettings

class PostgreSqlDatabaseSettings : DatabaseSettings {

    override val user = Environment.get("POSTGRESQL_DATABASE_USER")
    override val jdbcUrl = Environment.get("POSTGRESQL_DATABASE_JDBC_URL")
    override val password = Environment.get("POSTGRESQL_DATABASE_PASSWORD")
}