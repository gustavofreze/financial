package financial.shared.adapters.kafka

import org.apache.kafka.clients.admin.Admin
import org.apache.kafka.clients.admin.CreateTopicsResult
import org.apache.kafka.clients.admin.CreateTopicsResult.TopicMetadataAndConfig
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.common.KafkaFuture
import org.apache.kafka.common.errors.TopicAuthorizationException
import org.apache.kafka.common.errors.TopicExistsException
import org.apache.kafka.common.internals.KafkaFutureImpl
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.reflect.Proxy
import java.util.Properties
import java.util.concurrent.ExecutionException
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class KafkaTopicsTest {

    private val requested = mutableListOf<NewTopic>()

    private val configured = mutableListOf<Properties>()

    private var closed = false

    @Test
    fun `create when the topics are new then creates each one as named with one partition and one replica`() {
        /** @When the topics are created */
        topics(failure = null).create()

        /** @Then the topic is requested with one partition and one replica */
        assertEquals(listOf(NewTopic("financial.transaction-registered", 1, 1)), requested)

        /** @And the admin is configured for the broker with a ten second timeout */
        assertEquals("broker:9092", configured.single()["bootstrap.servers"])
        assertEquals(10000, configured.single()["request.timeout.ms"])

        /** @And the admin is closed */
        assertTrue(closed)
    }

    @Test
    fun `create when the topics already exist then succeeds`() {
        /** @When the topics are created on a broker that already holds them */
        topics(failure = TopicExistsException("exists")).create()

        /** @Then the topic is still requested once */
        assertEquals(1, requested.size)

        /** @And the admin is closed */
        assertTrue(closed)
    }

    @Test
    fun `create when the broker refuses the topics then fails with the cause`() {
        /** @Given topics on a broker that refuses them */
        val topics = topics(failure = TopicAuthorizationException("denied"))

        /** @When the topics are created */
        val actual = assertThrows<ExecutionException> { topics.create() }

        /** @Then the failure keeps the authorization cause */
        assertIs<TopicAuthorizationException>(actual.cause)

        /** @And the admin is closed */
        assertTrue(closed)
    }

    @Test
    fun `kafka admin when built from properties then is a client of the given broker`() {
        /** @Given properties naming a local broker */
        val properties = Properties().apply { this["bootstrap.servers"] = "127.0.0.1:1" }

        /** @When the admin is built from those properties */
        val actual = KafkaTopics.KAFKA_ADMIN(properties)

        /** @Then the admin is a client that exposes its metrics */
        actual.use { admin -> assertTrue(admin.metrics().isNotEmpty()) }
    }

    private fun topics(failure: Exception?) = KafkaTopics(
        admin = { properties ->
            configured.add(properties)
            admin(failure = failure)
        },
        settings = KafkaSettings(
            topics = listOf("financial.transaction-registered"),
            connectorUrl = "unused",
            applicationId = "financial",
            connectorsPath = "unused",
            bootstrapServers = "broker:9092",
            schemaRegistryUrl = "mock://unused"
        )
    )

    private fun admin(failure: Exception?): Admin =
        Proxy.newProxyInstance(Admin::class.java.classLoader, arrayOf(Admin::class.java)) { _, method, arguments ->
            when (method.name) {
                "createTopics" -> {
                    requested.addAll((arguments[0] as Collection<*>).filterIsInstance<NewTopic>())
                    val future = KafkaFutureImpl<TopicMetadataAndConfig>()
                    failure?.let { future.completeExceptionally(it) } ?: future.complete(null)
                    object : CreateTopicsResult(mapOf<String, KafkaFuture<TopicMetadataAndConfig>>("t" to future)) {}
                }

                "close" -> {
                    closed = true
                    null
                }

                else -> error("Unexpected call ${method.name}")
            }
        } as Admin
}
