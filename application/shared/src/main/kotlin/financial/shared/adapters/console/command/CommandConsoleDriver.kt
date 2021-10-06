package financial.shared.adapters.console.command

import financial.shared.adapters.console.command.bus.CommandBus

class CommandConsoleDriver(private val commands: Set<Command>, private val commandBus: CommandBus<Command>) {

    fun drive(args: Array<String>) {
        if (args.count() < 2) error("Invalid console command")

        commands
            .single { it::class.java.simpleName == args[1] }
            .also { commandBus.dispatch(it) }
    }
}