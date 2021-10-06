package financial.shared.streams

interface TopicFactory {

    suspend fun build(topics: List<Topic>)

    data class Topic(val name: String, val numPartitions: Int = 1, val replicationFactor: Int = 1)
}