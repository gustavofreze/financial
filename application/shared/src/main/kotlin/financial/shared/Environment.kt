package financial.shared

object Environment {

    fun get(variable: String): String = System.getenv(variable) ?: error("Environment variable $variable is missing")
}
