package financial.shared.adapters.kafka

class ConnectorNotRegistered(val name: String, val status: Int?) : IllegalStateException()
