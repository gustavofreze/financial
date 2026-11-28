package financial.shared.adapters.kafka

class KafkaConnectUnavailable(val attempts: Int) : IllegalStateException()
