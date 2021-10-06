package integration

import financial.shared.databases.RelationalDatabase
import financial.shared.databases.RelationalDatabaseCapabilities
import financial.shared.databases.postgresql.PostgreSqlDatabase
import mock.DatabaseConnectionForTesting
import mock.DatabaseSettingsForTesting

object PostgreSql {

    private var database: RelationalDatabase

    init {
        val connection = DatabaseConnectionForTesting(settings = DatabaseSettingsForTesting())

        database = PostgreSqlDatabase(RelationalDatabaseCapabilities(connection.jdbi))
        database.execute(Migration.CREATE_SCHEMA_ACCOUNT)
        database.execute(Migration.CREATE_TABLE_BALANCES)
        database.execute(Migration.CREATE_TABLE_TROUBLESHOOT)
        database.execute(Migration.INSERT_INTO_BALANCES)
    }

    fun load(): RelationalDatabase = database
}