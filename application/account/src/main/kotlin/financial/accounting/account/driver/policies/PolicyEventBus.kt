package financial.accounting.account.driver.policies

import financial.shared.application.EventBus
import financial.shared.application.ReceivedEvent

class PolicyEventBus(private val policies: List<Policy<*>>) : EventBus {

    override fun dispatch(event: ReceivedEvent) = policies.forEach { policy -> policy.handle(event = event) }
}
