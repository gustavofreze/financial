package financial.shared.databases.postgresql

import financial.shared.databases.RelationalDatabase

class PostgreSqlDatabase(private val database: RelationalDatabase) : RelationalDatabase by database