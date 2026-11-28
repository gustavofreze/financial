package financial.accounting.account

import financial.accounting.account.application.ports.outbound.PolicyReplay

class PolicyReplaySpy(override val name: String, private val failure: Exception?) : PolicyReplay {

    val replayed = mutableListOf<String>()

    override fun replay(payload: String) {
        replayed.add(payload)
        failure?.let { throw it }
    }
}
