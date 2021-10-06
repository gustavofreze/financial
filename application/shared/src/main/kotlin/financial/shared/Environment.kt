package financial.shared

import java.lang.System.getenv

object Environment {

    fun get(variable: String): String = getenv(variable) ?: error("Environment variable $variable is missing")
}