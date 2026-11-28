package financial.accounting.account.driver.console

import financial.accounting.account.application.commands.SolveFailures
import financial.accounting.account.application.ports.inbound.FailuresSolving
import financial.shared.adapters.console.ConsoleCommand

class SolveFailuresConsole(private val solving: FailuresSolving) : ConsoleCommand {

    override val name = "SolveFailures"

    override fun run() = solving.handle(command = SolveFailures())
}
