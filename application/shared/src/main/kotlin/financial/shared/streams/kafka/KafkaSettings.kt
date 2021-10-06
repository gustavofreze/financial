package financial.shared.streams.kafka

import financial.shared.Environment
import financial.shared.streams.StreamSettings

class KafkaSettings : StreamSettings {

    override val connectorUrl = Environment.get("KAFKA_CONNECT_URL")
    override val applicationId = Environment.get("APPLICATION_ID")
    override val connectorsPath = Environment.get("KAFKA_CONNECT_CONNECTORS_PATH")
    override val bootstrapServers = Environment.get("BOOTSTRAP_SERVERS")
    override val schemaRegistryUrl = Environment.get("SCHEMA_REGISTRY_URL")
}