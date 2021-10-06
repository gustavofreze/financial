package financial.shared.adapters.console.command.bus

import financial.shared.adapters.console.command.Command

interface CommandBus<T : Command> {

    fun dispatch(command: T)
}