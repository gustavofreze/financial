package financial.shared.streams

interface StreamSettings {

    val connectorUrl: String
    val applicationId: String
    val connectorsPath: String
    val bootstrapServers: String
    val schemaRegistryUrl: String
}