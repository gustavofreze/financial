package financial.starter

import financial.shared.Environment.get
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
        val topics = get("TOPICS")
        return topics.split(",").map { Topic(it.trim().lowercase()) }
    }
}