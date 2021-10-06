package financial.shared.streams.kafka.stream

import financial.shared.streams.StreamFactory
import financial.shared.streams.StreamSettings
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.kafka.common.serialization.Serdes.BytesSerde
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsConfig.*
import java.util.*

class KafkaStreamFactory(
    private val streamSettings: StreamSettings,
    private val kafkaTopologyFactory: KafkaTopologyFactory
) : StreamFactory {

    override suspend fun build() {
        val topology = kafkaTopologyFactory.build()

        KafkaStreams(topology, properties()).also {
            it.cleanUp()
            it.start()
            Runtime.getRuntime().addShutdownHook(Thread(it::close))
        }
    }

    private fun properties(): Properties {
        val properties = Properties()
        properties[APPLICATION_ID_CONFIG] = streamSettings.applicationId
        properties[BOOTSTRAP_SERVERS_CONFIG] = streamSettings.bootstrapServers
        properties[SCHEMA_REGISTRY_URL_CONFIG] = streamSettings.schemaRegistryUrl
        properties[DEFAULT_KEY_SERDE_CLASS_CONFIG] = BytesSerde::class.java
        properties[DEFAULT_VALUE_SERDE_CLASS_CONFIG] = GenericAvroSerde::class.java
        return properties
    }
}