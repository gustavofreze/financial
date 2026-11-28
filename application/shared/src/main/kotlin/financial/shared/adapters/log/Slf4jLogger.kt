package financial.shared.adapters.log

import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

class Slf4jLogger(owner: KClass<*>) : Logger {

    private val logger = LoggerFactory.getLogger(owner.java.name)

    override fun info(message: String) = logger.info(message)
}
