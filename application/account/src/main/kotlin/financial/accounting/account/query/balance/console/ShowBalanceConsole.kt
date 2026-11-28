package financial.accounting.account.query.balance.console

import financial.accounting.account.query.balance.BalanceFinding
import financial.shared.adapters.console.ConsoleCommand
import java.io.PrintStream

class ShowBalanceConsole(private val output: PrintStream, private val finding: BalanceFinding) : ConsoleCommand {

    override val name = "ShowBalance"

    override fun run() {
        output.format(SEPARATOR)
        output.format(ROW, "Account", "Balance", "Status")
        output.format(SEPARATOR)
        finding.findAll()
            .map { balance -> BalanceRow(balance = balance) }
            .forEach { row -> output.format(ROW, row.number, row.display, row.status) }
        output.format(SEPARATOR)
    }

    private companion object {
        const val ROW = "| %-12s | %-12s | %-12s |%n"
        const val SEPARATOR = "+--------------+--------------+--------------+%n"
    }
}
