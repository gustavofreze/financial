package financial.shared.adapters.console.event

import financial.shared.buildingblocks.domainevent.Event

interface EventBus<T : Event> {

    fun dispatch(event: T)
}