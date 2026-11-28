package financial.shared.adapters.kafka

import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG
import org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.streams.Topology
import java.util.Properties

class KafkaStreamsRunner(
    private val streams: (Topology, StreamsConfig) -> KafkaStreams,
    private val settings: KafkaSettings,
    private val shutdown: ShutdownHooks,
    private val topology: KafkaTopology
) {

    fun start() {
        val config = StreamsConfig(
            Properties().apply {
                this[APPLICATION_ID_CONFIG] = settings.applicationId
                this[BOOTSTRAP_SERVERS_CONFIG] = settings.bootstrapServers
                this[PROCESSING_EXCEPTION_HANDLER_GLOBAL_ENABLED] = true
                this[StreamsConfig.mainConsumerPrefix(PROCESSING_EXCEPTION_HANDLER_GLOBAL_ENABLED)] = true
            }
        )

        streams(topology.build(config = config), config).apply {
            cleanUp()
            start()
            shutdown.register(hook = Thread(this::close))
        }
    }

    companion object {
        val KAFKA_STREAMS: (Topology, StreamsConfig) -> KafkaStreams = { topology, config ->
            KafkaStreams(topology, config)
        }

        val RUNTIME_SHUTDOWN_HOOKS = ShutdownHooks { hook -> Runtime.getRuntime().addShutdownHook(hook) }

        private const val PROCESSING_EXCEPTION_HANDLER_GLOBAL_ENABLED = "processing.exception.handler.global.enabled"
    }
}
