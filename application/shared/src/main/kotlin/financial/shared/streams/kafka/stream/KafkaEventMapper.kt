package financial.shared.streams.kafka.stream

import financial.shared.buildingblocks.domainevent.Event
import org.apache.avro.generic.GenericRecord

interface KafkaEventMapper {

    val topic: String

    fun map(record: GenericRecord): Event?
}