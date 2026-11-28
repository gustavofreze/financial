package financial.shared.adapters.kafka

import financial.shared.adapters.json.JsonMapper
import financial.shared.adapters.log.Logger
import java.time.Duration
import kotlin.io.path.Path
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.walk

class KafkaConnectors(
    private val pause: Pause,
    private val logger: Logger,
    private val settings: KafkaSettings,
    private val jsonMapper: JsonMapper,
    private val retryDelay: Duration,
    private val maximumAttempts: Int
) {

    private val client = KafkaConnectClient(url = settings.connectorUrl)

    fun register() {
        var attempts = 1

        while (!client.succeeded(response = client.get(path = ""))) {
            if (attempts >= maximumAttempts) {
                throw KafkaConnectUnavailable(attempts = attempts)
            }

            logger.info(message = "Waiting connection with kafka connect")
            pause.pause(duration = retryDelay)
            attempts += 1
        }

        val registered = client.get(path = "/connectors")
            ?.takeIf { response -> client.succeeded(response = response) }
            ?.let { response -> jsonMapper.toObject(json = response.body(), type = Array<String>::class).toList() }
            .orEmpty()

        Path(settings.connectorsPath).walk()
            .filter { file ->
                file.extension == "json" && file.nameWithoutExtension !in registered
            }
            .forEach { connector ->
                val name = connector.nameWithoutExtension
                val response = client.post(body = connector, path = "/connectors")

                if (!client.succeeded(response = response)) {
                    throw ConnectorNotRegistered(name = name, status = response?.statusCode())
                }

                logger.info(message = "The connector '$name' has been registered")
            }
    }

    companion object {
        val THREAD_PAUSE = Pause { duration -> Thread.sleep(duration) }
    }
}
