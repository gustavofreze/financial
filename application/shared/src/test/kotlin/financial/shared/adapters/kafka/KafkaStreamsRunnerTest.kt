package financial.shared.adapters.kafka

import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsConfig
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KafkaStreamsRunnerTest {

    private val settings = KafkaSettings(
        topics = listOf("financial.accounting.bookkeeping.transaction-registered"),
        connectorUrl = "unused",
        applicationId = "financial-test",
        connectorsPath = "unused",
        bootstrapServers = "127.0.0.1:1",
        schemaRegistryUrl = "mock://kafka-streams-runner-test"
    )

    private val calls = mutableListOf<String>()

    private val configs = mutableListOf<StreamsConfig>()

    private val hooks = mutableListOf<Thread>()

    private val runner = KafkaStreamsRunner(
        streams = { topology, config ->
            configs.add(config)
            RecordingKafkaStreams(calls = calls, config = config, topology = topology)
        },
        settings = settings,
        shutdown = { hook -> hooks.add(hook) },
        topology = topology()
    )

    @AfterEach
    fun stop() = hooks.forEach(Thread::run)

    @Test
    fun `start when called then cleans the local state and starts the streams with the settings`() {
        /** @When the runner starts */
        runner.start()

        /** @Then the streams are configured with the application id and the bootstrap servers */
        val config = configs.single()
        assertEquals("financial-test", config.getString(StreamsConfig.APPLICATION_ID_CONFIG))
        assertEquals(listOf("127.0.0.1:1"), config.getList(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG))

        /** @And the global processing exception handlers are enabled */
        assertEquals(true, config.originals()["processing.exception.handler.global.enabled"])
        assertEquals(true, config.originals()["main.consumer.processing.exception.handler.global.enabled"])

        /** @And the local state is cleaned before the streams start */
        assertEquals(listOf("cleanUp", "start"), calls)

        /** @And one shutdown hook is registered */
        assertEquals(1, hooks.size)
    }

    @Test
    fun `start when the process stops then the registered hook closes the streams`() {
        /** @Given a started runner */
        runner.start()

        /** @When the registered hook runs */
        hooks.single().run()

        /** @Then the streams are closed after they started */
        assertEquals(listOf("cleanUp", "start", "close"), calls)
    }

    @Test
    fun `start when the default factory builds the streams then registers the hook that closes them`() {
        /** @Given a runner on the default streams factory */
        val runner = KafkaStreamsRunner(
            streams = KafkaStreamsRunner.KAFKA_STREAMS,
            settings = settings,
            shutdown = { hook -> hooks.add(hook) },
            topology = topology()
        )

        /** @When the runner starts */
        runner.start()

        /** @Then one shutdown hook is registered */
        assertEquals(1, hooks.size)
    }

    @Test
    fun `kafka streams when built from a topology then is created and not started`() {
        /** @Given a streams config for the test application */
        val config = StreamsConfig(
            mapOf(
                StreamsConfig.APPLICATION_ID_CONFIG to settings.applicationId,
                StreamsConfig.BOOTSTRAP_SERVERS_CONFIG to settings.bootstrapServers
            )
        )

        /** @And a topology built with that config */
        val topology = topology().build(config = config)

        /** @When the default factory builds the streams */
        val actual = KafkaStreamsRunner.KAFKA_STREAMS(topology, config)

        /** @Then the streams are created and not started */
        actual.use { streams -> assertEquals(KafkaStreams.State.CREATED, streams.state()) }
    }

    @Test
    fun `runtime shutdown hooks when a hook is registered then the process holds it`() {
        /** @Given a hook that does nothing */
        val hook = Thread {}

        /** @When the hook is registered on the runtime */
        KafkaStreamsRunner.RUNTIME_SHUTDOWN_HOOKS.register(hook = hook)

        /** @Then the runtime holds the hook */
        assertTrue(Runtime.getRuntime().removeShutdownHook(hook))
    }

    private fun topology() =
        KafkaTopology(topics = settings.topics, eventBus = {}, schemaRegistryUrl = settings.schemaRegistryUrl)
}
