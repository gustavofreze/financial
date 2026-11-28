package financial.starter

import financial.shared.adapters.kafka.KafkaConnectors
import financial.shared.adapters.kafka.KafkaStreamsRunner
import financial.shared.adapters.kafka.KafkaTopics

class Platform(
    private val topics: KafkaTopics,
    private val streams: KafkaStreamsRunner,
    private val connectors: KafkaConnectors
) {

    fun run() {
        topics.create()
        connectors.register()
        streams.start()
    }
}
