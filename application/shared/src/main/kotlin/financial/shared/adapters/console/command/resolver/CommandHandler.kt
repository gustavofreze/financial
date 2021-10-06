package financial.shared.adapters.console.command.resolver

import financial.shared.adapters.console.command.Command

interface CommandHandler<T : Command> {

    fun handle(command: T)
}