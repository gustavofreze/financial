package financial.shared.adapters.database

import com.zaxxer.hikari.HikariConfig

class DatabaseSettings(private val user: String, private val jdbcUrl: String, private val password: String) {

    fun pool(): HikariConfig = HikariConfig().also { config ->
        config.jdbcUrl = jdbcUrl
        config.username = user
        config.password = password
        config.minimumIdle = MINIMUM_IDLE
        config.maximumPoolSize = MAXIMUM_POOL_SIZE
    }

    private companion object {
        const val MINIMUM_IDLE = 1
        const val MAXIMUM_POOL_SIZE = 4
    }
}
