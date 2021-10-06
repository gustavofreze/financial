package financial.shared.log

import kotlin.reflect.KClass

interface Logger {

    fun <T : Any> loggerFor(clazz: KClass<T>): Logger

    fun info(message: String)

    fun error(message: String)
}