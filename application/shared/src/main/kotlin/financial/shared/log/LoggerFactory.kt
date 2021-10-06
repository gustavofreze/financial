package financial.shared.log

import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import org.slf4j.Logger as LoggerSlf4j

class LoggerFactory : Logger {

    private lateinit var logger: LoggerSlf4j

    override fun <T : Any> loggerFor(clazz: KClass<T>): Logger {
        logger = LoggerFactory.getLogger(clazz.qualifiedName)
        return this
    }

    override fun info(message: String) {
        logger.info(message)
    }

    override fun error(message: String) {
        logger.error(message)
    }
}