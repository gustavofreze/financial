package financial.shared.adapters.kafka

import financial.shared.application.ReceivedEvent
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.TopologyTestDriver
import org.junit.jupiter.api.Test
import java.util.Properties
import kotlin.test.assertEquals

class KafkaTopologyTest {

    private val topic = "financial.accounting.bookkeeping.transaction-registered"

    private val schemaRegistryUrl = "mock://kafka-topology-test"

    @Test
    fun `build when a record arrives then dispatches its schema name and payload as a received event`() {
        /** @Given an event bus that records the dispatched events */
        val dispatched = mutableListOf<ReceivedEvent>()

        /** @And a streams config for the test application */
        val config = StreamsConfig(
            Properties().apply {
                this[StreamsConfig.APPLICATION_ID_CONFIG] = "financial-test"
                this[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "unused:9092"
            }
        )

        /** @And a topology over the transaction registered topic */
        val topology = KafkaTopology(
            topics = listOf(topic),
            eventBus = { event -> dispatched.add(event) },
            schemaRegistryUrl = schemaRegistryUrl
        )

        /** @And an avro serde bound to the mock schema registry */
        val values = GenericAvroSerde().apply {
            configure(mapOf(SCHEMA_REGISTRY_URL_CONFIG to schemaRegistryUrl), false)
        }

        /** @When a transaction registered record is piped into the topic */
        val properties = Properties().apply { putAll(config.originals()) }
        TopologyTestDriver(topology.build(config = config), properties).use { driver ->
            val record = record(name = "TransactionRegistered", payload = """{"id":"t1"}""")
            driver.createInputTopic(topic, Serdes.Bytes().serializer(), values.serializer())
                .pipeInput(Bytes.wrap(byteArrayOf(1)), record)
        }

        /** @Then a single event is dispatched with the schema name */
        val actual = dispatched.single()
        assertEquals("TransactionRegistered", actual.name)

        /** @And the event carries the record payload */
        assertEquals("""{"id":"t1"}""", actual.payload)
    }

    private fun record(name: String, payload: String): GenericRecord {
        val field = Schema.Field("payload", Schema.create(Schema.Type.STRING))
        val schema = Schema.createRecord(name, null, "financial", false, listOf(field))
        return GenericData.Record(schema).apply { put("payload", payload) }
    }
}
