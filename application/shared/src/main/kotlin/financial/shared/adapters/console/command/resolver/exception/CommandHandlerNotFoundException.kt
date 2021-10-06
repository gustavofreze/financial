package financial.shared.adapters.console.command.resolver.exception

class CommandHandlerNotFoundException(path: String) : ClassNotFoundException() {

    override val message = "No CommandHandler found in path $path"
}