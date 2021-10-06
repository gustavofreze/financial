package mock

import financial.shared.log.Logger
import kotlin.reflect.KClass

class LoggerMock : Logger {

    override fun <T : Any> loggerFor(clazz: KClass<T>): Logger = this

    override fun info(message: String) {}

    override fun error(message: String) {}
}