package financial.accounting.account.query

import financial.accounting.account.driven.view.Viewer
import financial.shared.adapters.console.command.resolver.CommandHandler

class ShowBalanceHandler(private val viewer: Viewer, private val balanceDao: BalanceDao) : CommandHandler<ShowBalance> {

    override fun handle(command: ShowBalance) {
        val accountBalances = balanceDao.findAll()

        viewer.show(accountBalances)
    }
}