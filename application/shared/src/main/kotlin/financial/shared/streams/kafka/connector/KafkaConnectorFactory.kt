package financial.shared.streams.kafka.connector

import com.fasterxml.jackson.databind.JsonNode
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.jackson.responseObject
import com.github.kittinunf.result.Result
import financial.shared.log.Logger
import financial.shared.streams.ConnectorFactory
import financial.shared.streams.ConnectorFactory.Connector
import financial.shared.streams.StreamSettings
import kotlinx.coroutines.delay
import java.nio.file.Files
import java.nio.file.Paths

class KafkaConnectorFactory(private val logger: Logger, private val streamSettings: StreamSettings) : ConnectorFactory {

    private val url = streamSettings.connectorUrl

    override suspend fun register() {
        waitingForConnection()

        val connectors = listFromBroker()

        listFromClasspath()
            .filter { !connectors.contains(it.name) }
            .forEach {
                val (_, _, result) = Fuel
                    .post(path = "${url}/connectors")
                    .body(it.data)
                    .appendHeader("Content-Type", "application/json")
                    .responseString()

                when (result) {
                    is Result.Success -> logger
                        .loggerFor(this::class)
                        .info("The connector '${it.name}' has been registered")
                    is Result.Failure -> logger
                        .loggerFor(this::class)
                        .error("The connector '${it.name}' has not been registered - (${result.error.message})")
                }
            }
    }

    private suspend fun waitingForConnection() {
        while (true) {
            val (_, _, result) = Fuel
                .get(path = url)
                .appendHeader("Content-Type", "application/json")
                .responseString()

            when (result) {
                is Result.Success -> break
                is Result.Failure -> {
                    logger.loggerFor(this::class).info("Waiting connection with kafka connect")
                    delay(3000L)
                }
            }
        }
    }

    private fun listFromBroker(): List<String> {
        val (_, _, result) = Fuel
            .get(path = "${url}/connectors", parameters = listOf("expand" to "status"))
            .responseObject<JsonNode>()

        return when (result) {
            is Result.Failure -> emptyList()
            is Result.Success -> result
                .value
                .fields()
                .asSequence()
                .map { it.value["status"] }
                .map { it["name"].asText() }
                .toList()
        }
    }

    private fun listFromClasspath(): List<Connector> {
        return Files
            .walk(Paths.get(streamSettings.connectorsPath))
            .filter { Files.isRegularFile(it) }
            .map { it.toFile() }
            .filter { it.name.endsWith(".json") }
            .map { Connector(name = it.name.removeSuffix(".json"), data = it.readText()) }
            .toList()
    }
}