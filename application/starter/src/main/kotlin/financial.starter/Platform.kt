package financial.starter

import financial.shared.Environment
import financial.shared.streams.ConnectorFactory
import financial.shared.streams.StreamFactory
import financial.shared.streams.TopicFactory
import financial.shared.streams.TopicFactory.Topic

class Platform(
    private val topicFactory: TopicFactory,
    private val streamFactory: StreamFactory,
    private val connectorFactory: ConnectorFactory
) {
    suspend fun run() {
        topicFactory.build(topics())
        connectorFactory.register()
        streamFactory.build()
    }

    private fun topics(): List<Topic> {
        val topics = Environment.get("TOPICS")
        return topics.split(",").map { Topic(it.trim().lowercase()) }
    }
}