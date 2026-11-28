package financial.shared.adapters.kafka

import com.sun.net.httpserver.HttpServer
import financial.shared.adapters.json.JacksonJsonMapper
import financial.shared.testing.LoggerSpy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.nio.file.Path
import java.time.Duration
import kotlin.io.path.writeText
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KafkaConnectorsTest {

    private val posted = mutableListOf<String>()

    private val probes = ArrayDeque(listOf(503, 200))

    private var listingStatus = 200

    private val pauses = mutableListOf<Duration>()

    private val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0).apply {
        createContext("/") { exchange ->
            val path = exchange.requestURI.path
            val (status, body) = when {
                exchange.requestMethod == "POST" -> {
                    val body = exchange.requestBody.readAllBytes().decodeToString().also { posted.add(it) }
                    val status = when {
                        body.contains("dropped") -> DROPPED
                        body.contains("rejected") -> 409
                        else -> 201
                    }
                    status to body
                }

                path == "/connectors" -> listingStatus to """["already-registered"]"""

                else -> (probes.removeFirstOrNull() ?: 200) to "{}"
            }
            if (status == DROPPED) {
                exchange.close()
            } else {
                exchange.sendResponseHeaders(status, body.length.toLong())
                exchange.responseBody.use { stream -> stream.write(body.toByteArray()) }
            }
        }
        start()
    }

    @AfterEach
    fun stop() = server.stop(0)

    @Test
    fun `register when kafka connect answers after a retry then posts only the connectors not registered`(
        @TempDir path: Path
    ) {
        /** @Given a connector file already registered in kafka connect */
        path.resolve("already-registered.json").writeText("""{"name":"already-registered"}""")

        /** @And a new connector file beside a file that is not a connector */
        path.resolve("new-source.json").writeText("""{"name":"new-source"}""")
        path.resolve("notes.txt").writeText("ignored")

        /** @And a logger that records the info lines */
        val logger = LoggerSpy()

        /** @When the connectors are registered while kafka connect answers after one retry */
        connectors(path = path, logger = logger, retryDelay = Duration.ZERO).register()

        /** @Then only the new connector is posted */
        assertEquals(listOf("""{"name":"new-source"}"""), posted)

        /** @And the logger tells the wait and the registration of the new connector */
        val expected = listOf("Waiting connection with kafka connect", "The connector 'new-source' has been registered")
        assertEquals(expected, logger.infos)
    }

    @Test
    fun `register when the listing answers at the edge of success then still skips the registered connectors`(
        @TempDir path: Path
    ) {
        /** @Given a listing of the registered connectors that answers 299 */
        listingStatus = 299

        /** @And the file of a connector already registered */
        path.resolve("already-registered.json").writeText("""{"name":"already-registered"}""")

        /** @When the connectors are registered */
        connectors(path = path, logger = LoggerSpy(), retryDelay = Duration.ZERO).register()

        /** @Then no connector is posted */
        assertTrue(posted.isEmpty())
    }

    @Test
    fun `register when the listing fails then posts every connector on disk`(@TempDir path: Path) {
        /** @Given a listing of the registered connectors that answers 300 */
        listingStatus = 300

        /** @And the file of a connector already registered */
        path.resolve("already-registered.json").writeText("""{"name":"already-registered"}""")

        /** @When the connectors are registered */
        connectors(path = path, logger = LoggerSpy(), retryDelay = Duration.ZERO).register()

        /** @Then the connector on disk is posted */
        assertEquals(listOf("""{"name":"already-registered"}"""), posted)
    }

    @Test
    fun `register when kafka connect is not ready then pauses the retry delay before probing again`(
        @TempDir path: Path
    ) {
        /** @Given a retry delay of three seconds */
        val retryDelay = Duration.ofSeconds(3)

        /** @When the connectors are registered while kafka connect is not ready */
        connectors(path = path, logger = LoggerSpy(), retryDelay = retryDelay).register()

        /** @Then the retry delay is paused once before the next probe */
        assertEquals(listOf(retryDelay), pauses)
    }

    @Test
    fun `thread pause when asked then holds the running thread for the duration`() {
        /** @Given a pause of fifty milliseconds */
        val duration = Duration.ofMillis(50)

        /** @And the instant the pause starts */
        val started = System.nanoTime()

        /** @When the running thread pauses for that duration */
        KafkaConnectors.THREAD_PAUSE.pause(duration = duration)

        /** @Then at least that duration has elapsed */
        assertTrue(Duration.ofNanos(System.nanoTime() - started) >= duration)
    }

    @Test
    fun `register when kafka connect is ready at once then registers without waiting`(@TempDir path: Path) {
        /** @Given kafka connect ready at the first probe */
        probes.clear()

        /** @And a new connector file on disk */
        path.resolve("new-source.json").writeText("""{"name":"new-source"}""")

        /** @And a logger and connectors allowed a single attempt */
        val logger = LoggerSpy()
        val connectors = connectors(path = path, logger = logger, retryDelay = Duration.ZERO, maximumAttempts = 1)

        /** @When the connectors are registered */
        connectors.register()

        /** @Then only the registration of the new connector is logged */
        assertEquals(listOf("The connector 'new-source' has been registered"), logger.infos)
    }

    @Test
    fun `register when a connector is rejected then fails with its name and status`(@TempDir path: Path) {
        /** @Given a connector file kafka connect rejects */
        path.resolve("rejected-source.json").writeText("""{"name":"rejected"}""")
        val connectors = connectors(path = path, logger = LoggerSpy(), retryDelay = Duration.ZERO)

        /** @When the connectors are registered */
        val actual = assertThrows<ConnectorNotRegistered> { connectors.register() }

        /** @Then the failure names the rejected connector */
        assertEquals("rejected-source", actual.name)

        /** @And the failure carries the conflict status */
        assertEquals(409, actual.status)
    }

    @Test
    fun `register when kafka connect never answers then fails with the attempts`(@TempDir path: Path) {
        /** @Given kafka connect unavailable for the first two probes */
        probes.addFirst(503)

        /** @And connectors allowed two attempts */
        val connectors = connectors(path = path, logger = LoggerSpy(), retryDelay = Duration.ZERO, maximumAttempts = 2)

        /** @When the connectors are registered */
        val actual = assertThrows<KafkaConnectUnavailable> { connectors.register() }

        /** @Then the failure counts two attempts */
        assertEquals(2, actual.attempts)

        /** @And the retry delay is paused once between the attempts */
        assertEquals(listOf(Duration.ZERO), pauses)
    }

    @Test
    fun `register when kafka connect cannot be reached then pauses and fails with the attempts`(@TempDir path: Path) {
        /** @Given a local port nothing listens on */
        val port = ServerSocket(0).use { socket -> socket.localPort }

        /** @And connectors pointing kafka connect at that port with two attempts */
        val connectors = KafkaConnectors(
            pause = { duration -> record(pause = duration) },
            logger = LoggerSpy(),
            settings = settings(path = path).copy(connectorUrl = "http://127.0.0.1:$port"),
            jsonMapper = JacksonJsonMapper(),
            retryDelay = Duration.ZERO,
            maximumAttempts = 2
        )

        /** @When the connectors are registered */
        val actual = assertThrows<KafkaConnectUnavailable> { connectors.register() }

        /** @Then the failure counts two attempts */
        assertEquals(2, actual.attempts)

        /** @And the retry delay is paused once between the attempts */
        assertEquals(listOf(Duration.ZERO), pauses)
    }

    @Test
    fun `register when kafka connect drops the listing then posts every connector on disk`(@TempDir path: Path) {
        /** @Given a listing that kafka connect drops */
        listingStatus = DROPPED

        /** @And the file of a connector already registered */
        path.resolve("already-registered.json").writeText("""{"name":"already-registered"}""")
        val connectors = connectors(path = path, logger = LoggerSpy(), retryDelay = Duration.ZERO)

        /** @When the connectors are registered */
        connectors.register()

        /** @Then the connector on disk is posted */
        assertEquals(listOf("""{"name":"already-registered"}"""), posted)
    }

    @Test
    fun `register when kafka connect drops a registration then fails with no status`(@TempDir path: Path) {
        /** @Given a connector file whose registration kafka connect drops */
        path.resolve("dropped-source.json").writeText("""{"name":"dropped"}""")
        val connectors = connectors(path = path, logger = LoggerSpy(), retryDelay = Duration.ZERO)

        /** @When the connectors are registered */
        val actual = assertThrows<ConnectorNotRegistered> { connectors.register() }

        /** @Then the failure names the dropped connector */
        assertEquals("dropped-source", actual.name)

        /** @And the failure carries no status */
        assertNull(actual.status)
    }

    private fun record(pause: Duration) {
        pauses.add(pause)

        if (pauses.size > MAXIMUM_PAUSES) {
            throw IllegalStateException()
        }
    }

    private fun connectors(path: Path, logger: LoggerSpy, retryDelay: Duration, maximumAttempts: Int = 60) =
        KafkaConnectors(
            pause = { duration -> record(pause = duration) },
            logger = logger,
            settings = settings(path = path),
            jsonMapper = JacksonJsonMapper(),
            retryDelay = retryDelay,
            maximumAttempts = maximumAttempts
        )

    private fun settings(path: Path) = KafkaSettings(
        topics = emptyList(),
        connectorUrl = "http://127.0.0.1:${server.address.port}",
        applicationId = "financial",
        connectorsPath = path.toString(),
        bootstrapServers = "unused:9092",
        schemaRegistryUrl = "mock://unused"
    )

    private companion object {
        const val DROPPED = -1
        const val MAXIMUM_PAUSES = 10
    }
}
