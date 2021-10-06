package financial.shared.adapters.console.command.resolver

import financial.shared.adapters.console.command.Command
import financial.shared.adapters.console.command.resolver.exception.CommandHandlerNotFoundException
import financial.shared.di.Container

class CommandHandlerResolver(private val container: Container) : CommandResolver {

    @Suppress("UNCHECKED_CAST")
    override fun resolve(command: Command): CommandHandler<Command> {
        val className = "%sHandler"
            .format(command.javaClass.kotlin)
            .removePrefix("class")
            .trim()

        try {
            return container.get(className)
        } catch (exception: ClassNotFoundException) {
            throw CommandHandlerNotFoundException(className)
        }
    }
}