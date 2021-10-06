package financial.shared.adapters.console.event

import financial.shared.buildingblocks.domainevent.Event
import financial.shared.policies.Policy
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class EventDispatcher(private val policies: Set<Policy<*>>) : EventBus<Event> {

    override fun dispatch(event: Event) = runBlocking {
        policiesOf(event)
            .map { policy -> async { policy.handle(event) } }
            .forEach { it.await() }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Event> policiesOf(event: T): List<Policy<T>> {
        return policies
            .filter { policy -> policy.subscribedEvent.isInstance(event) }
            .map { it as Policy<T> }
    }
}