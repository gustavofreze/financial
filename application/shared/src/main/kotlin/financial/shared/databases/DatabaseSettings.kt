package financial.shared.databases

interface DatabaseSettings {

    val user: String
    val jdbcUrl: String
    val password: String
}