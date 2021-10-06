package financial.shared.eventsourcing

import financial.shared.buildingblocks.entity.Identity
import financial.shared.valueobject.UUID
import financial.shared.valueobject.Version
import java.time.LocalDateTime
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.javaMethod

class EventSourcingCapabilities<T> : EventSourcing<T> {

    override val recordedEvents = mutableListOf<EventRecord>()

    override fun onEvent(event: Event, identity: Identity, sequence: Version, entity: EventSourcingRoot<T>) {
        EventRecord(
            code = UUID.generateAsString(),
            event = event,
            revision = event.revision(),
            sequence = sequence.next(),
            eventName = event.type(),
            occurredOn = LocalDateTime.now(),
            aggregateId = identity.value.toString(),
            aggregateType = entity::class.java.simpleName
        ).also {
            applyEvent(it, entity)
            recordedEvents.add(it)
        }
    }

    override fun applyEvent(eventRecord: EventRecord, entity: EventSourcingRoot<T>) {
        val event = eventRecord.event

        entity::class.functions
            .find { it.name == eventName(event) }
            ?.let { it.javaMethod?.invoke(entity, event) }
    }

    private fun eventName(event: Event): String {
        return "on%s"
            .format(event::class.simpleName)
            .removePrefix("class")
            .trim()
    }
}