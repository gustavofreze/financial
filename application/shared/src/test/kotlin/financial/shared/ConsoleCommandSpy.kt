package financial.shared

import financial.shared.adapters.console.ConsoleCommand

class ConsoleCommandSpy(override val name: String) : ConsoleCommand {

    var runs = 0
        private set

    override fun run() {
        runs++
    }
}
