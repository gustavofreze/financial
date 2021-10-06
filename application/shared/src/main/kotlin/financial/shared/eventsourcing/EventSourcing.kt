package financial.shared.eventsourcing

import financial.shared.buildingblocks.entity.Identity
import financial.shared.valueobject.Version

interface EventSourcing<T> {

    val recordedEvents: MutableList<EventRecord>

    fun onEvent(event: Event, identity: Identity, sequence: Version, entity: EventSourcingRoot<T>)

    fun applyEvent(eventRecord: EventRecord, entity: EventSourcingRoot<T>)
}