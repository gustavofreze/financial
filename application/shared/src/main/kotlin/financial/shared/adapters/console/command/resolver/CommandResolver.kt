package financial.shared.adapters.console.command.resolver

import financial.shared.adapters.console.command.Command

interface CommandResolver {

    fun resolve(command: Command): CommandHandler<Command>
}