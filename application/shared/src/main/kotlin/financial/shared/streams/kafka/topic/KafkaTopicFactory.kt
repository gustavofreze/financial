package financial.shared.streams.kafka.topic

import financial.shared.streams.StreamSettings
import financial.shared.streams.TopicFactory
import financial.shared.streams.TopicFactory.Topic
import org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.admin.AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG
import org.apache.kafka.clients.admin.KafkaAdminClient
import org.apache.kafka.clients.admin.NewTopic
import java.util.*

class KafkaTopicFactory(private val streamSettings: StreamSettings) : TopicFactory {

    override suspend fun build(topics: List<Topic>) {
        val kafkaAdminClient = KafkaAdminClient.create(properties())
        val newTopics = topics.map { NewTopic(it.name, it.numPartitions, it.replicationFactor.toShort()) }

        kafkaAdminClient.createTopics(newTopics)
        kafkaAdminClient.close()
    }

    private fun properties(): Properties {
        val properties = Properties()
        properties[BOOTSTRAP_SERVERS_CONFIG] = streamSettings.bootstrapServers
        properties[REQUEST_TIMEOUT_MS_CONFIG] = 10000
        return properties
    }
}