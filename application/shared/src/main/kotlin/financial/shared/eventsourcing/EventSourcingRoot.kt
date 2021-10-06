package financial.shared.eventsourcing

import financial.shared.buildingblocks.entity.Entity

interface EventSourcingRoot<T> : Entity {

    fun reconstitute(recordedEvents: List<EventRecord>)
}