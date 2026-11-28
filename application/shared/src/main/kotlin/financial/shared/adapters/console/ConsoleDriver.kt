package financial.shared.adapters.console

class ConsoleDriver(commands: List<ConsoleCommand>) {

    private val commands = commands.associateBy(ConsoleCommand::name)

    fun drive(name: String) = (commands[name] ?: error("Invalid console command $name")).run()
}
