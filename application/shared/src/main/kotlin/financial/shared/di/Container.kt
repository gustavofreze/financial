package financial.shared.di

interface Container {

    fun <T> get(className: String): T

    fun has(className: String): Boolean
}