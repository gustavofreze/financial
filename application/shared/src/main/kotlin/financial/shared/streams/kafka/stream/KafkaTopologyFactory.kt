package financial.shared.streams.kafka.stream

import financial.shared.adapters.console.event.EventBus
import financial.shared.buildingblocks.domainevent.Event
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology

class KafkaTopologyFactory(private val mappers: Set<KafkaEventMapper>, private val eventBus: EventBus<Event>) {

    fun build(): Topology {
        val builder = StreamsBuilder()

        mappers.forEach { mapper ->
            builder
                .stream<Bytes, GenericRecord>(mapper.topic)
                .mapValues { record -> mapper.map(record) }
                .foreach { _, event -> event?.let { eventBus.dispatch(it) } }
        }
        return builder.build()
    }
}