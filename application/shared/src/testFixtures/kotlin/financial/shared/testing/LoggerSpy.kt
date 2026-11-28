package financial.shared.testing

import financial.shared.adapters.log.Logger

class LoggerSpy : Logger {

    val infos = mutableListOf<String>()

    override fun info(message: String) {
        infos.add(message)
    }
}
