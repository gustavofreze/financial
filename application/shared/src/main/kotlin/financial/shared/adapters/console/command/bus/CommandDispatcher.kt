package financial.shared.adapters.console.command.bus

import financial.shared.adapters.console.command.Command
import financial.shared.adapters.console.command.resolver.CommandHandler
import financial.shared.adapters.console.command.resolver.CommandResolver

class CommandDispatcher(private val commandResolver: CommandResolver) : CommandBus<Command> {

    override fun dispatch(command: Command) {
        val commandHandler: CommandHandler<Command> = commandResolver.resolve(command)
        commandHandler.handle(command)
    }
}