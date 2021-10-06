package financial.shared.streams

interface ConnectorFactory {

    suspend fun register()

    data class Connector(val name: String, val data: String)
}