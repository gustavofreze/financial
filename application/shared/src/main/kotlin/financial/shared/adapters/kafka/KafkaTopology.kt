package financial.shared.adapters.kafka

import financial.shared.application.EventBus
import financial.shared.application.ReceivedEvent
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.TopologyConfig
import org.apache.kafka.streams.kstream.Consumed

class KafkaTopology(
    private val topics: List<String>,
    private val eventBus: EventBus,
    private val schemaRegistryUrl: String
) {

    fun build(config: StreamsConfig): Topology {
        val builder = StreamsBuilder(TopologyConfig(config))
        val values = GenericAvroSerde().apply {
            configure(mapOf(SCHEMA_REGISTRY_URL_CONFIG to schemaRegistryUrl), false)
        }

        builder
            .stream(topics, Consumed.with(Serdes.Bytes(), values))
            .mapValues { record ->
                ReceivedEvent(name = record.schema.name, payload = record.get("payload").toString())
            }
            .foreach { _, event -> eventBus.dispatch(event = event) }

        return builder.build()
    }
}
