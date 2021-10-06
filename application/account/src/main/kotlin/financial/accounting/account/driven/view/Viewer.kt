package financial.accounting.account.driven.view

import financial.accounting.account.core.model.AccountBalance

interface Viewer {

    fun show(accountBalances: List<AccountBalance>)
}