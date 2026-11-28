package financial.shared.adapters.kafka

data class KafkaSettings(
    val topics: List<String>,
    val connectorUrl: String,
    val applicationId: String,
    val connectorsPath: String,
    val bootstrapServers: String,
    val schemaRegistryUrl: String
)
