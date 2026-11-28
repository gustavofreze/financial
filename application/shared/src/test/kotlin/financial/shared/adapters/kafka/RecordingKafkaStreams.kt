package financial.shared.adapters.kafka

import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.Topology

class RecordingKafkaStreams(private val calls: MutableList<String>, config: StreamsConfig, topology: Topology) :
    KafkaStreams(topology, config) {

    override fun cleanUp() {
        calls.add("cleanUp")
    }

    override fun start() {
        calls.add("start")
    }

    override fun close() {
        calls.add("close")
        super.close()
    }
}
