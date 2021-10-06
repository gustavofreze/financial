package mock

import financial.accounting.account.command.adjust.AdjustBalance
import financial.shared.adapters.console.command.bus.CommandBus

class AdjustBalanceBusMock : CommandBus<AdjustBalance> {

    private val dispatched: MutableList<AdjustBalance> = mutableListOf()

    fun commands(): List<AdjustBalance> = dispatched.toList()

    override fun dispatch(command: AdjustBalance) {
        dispatched.add(command)
    }
}