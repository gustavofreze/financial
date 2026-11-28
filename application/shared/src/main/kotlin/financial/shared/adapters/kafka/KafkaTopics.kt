package financial.shared.adapters.kafka

import org.apache.kafka.clients.admin.Admin
import org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.admin.AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.common.errors.TopicExistsException
import java.util.Properties
import java.util.concurrent.ExecutionException

class KafkaTopics(private val admin: (Properties) -> Admin, private val settings: KafkaSettings) {

    fun create() {
        val properties = Properties().apply {
            this[BOOTSTRAP_SERVERS_CONFIG] = settings.bootstrapServers
            this[REQUEST_TIMEOUT_MS_CONFIG] = REQUEST_TIMEOUT_MS
        }
        val topics = settings.topics.map { topic -> NewTopic(topic, PARTITIONS, REPLICATION_FACTOR) }

        admin(properties).use { client ->
            try {
                client.createTopics(topics).all().get()
            } catch (exception: ExecutionException) {
                if (exception.cause !is TopicExistsException) {
                    throw exception
                }
            }
        }
    }

    companion object {
        val KAFKA_ADMIN: (Properties) -> Admin = { properties -> Admin.create(properties) }

        private const val PARTITIONS = 1
        private const val REQUEST_TIMEOUT_MS = 10000
        private const val REPLICATION_FACTOR: Short = 1
    }
}
