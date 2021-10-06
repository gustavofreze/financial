package unit.financial.accounting.account.driver.kafka

import commonvalues.Accounts.CREDIT_ACCOUNT_IN_BRL
import commonvalues.Accounts.CREDIT_ACCOUNT_IN_USD
import commonvalues.Accounts.DEBIT_ACCOUNT_IN_BRL
import commonvalues.Accounts.DEBIT_ACCOUNT_IN_USD
import financial.accounting.account.driver.kafka.TransactionRegisteredMapper
import financial.accounting.account.driver.policies.events.TransactionRegistered
import financial.shared.mapper.JsonMapperFactory
import financial.shared.valueobject.Amount
import financial.shared.valueobject.Currency
import financial.shared.valueobject.UUID
import mock.LoggerMock
import mock.SchemaMock
import org.apache.avro.generic.GenericRecordBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDateTime
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TransactionRegisteredMapperTest {

    private val payload = """
        {
            "id": "%s",
            "amount": {
                "value": %s,
                "currency": "%s"
            },
            "status": 1,
            "debitAccount": "%s",
            "creditAccount": "%s"
        }
    """.trimIndent()

    private lateinit var transactionRegisteredMapper: TransactionRegisteredMapper

    @BeforeEach
    fun `Before each`() {
        val logger = LoggerMock()
        val jsonMapper = JsonMapperFactory()

        transactionRegisteredMapper = TransactionRegisteredMapper(logger = logger, jsonMapper = jsonMapper)
    }

    @ParameterizedTest
    @MethodSource("transactionRegisteredProvider")
    fun `GIVEN that I have a valid RegisteredTransaction event, WHEN the map method is called, THEN return the event`(
        expected: TransactionRegistered
    ) {
        val id = expected.id
        val value = expected.amount.value
        val currency = expected.amount.currency
        val debitAccount = expected.debitAccount
        val creditAccount = expected.creditAccount

        val name = TransactionRegistered::class.simpleName!!
        val schema = SchemaMock.get(name)
        val record = GenericRecordBuilder(schema)
            .set("code", UUID.generateAsString())
            .set("payload", payload.format(id, value, currency, debitAccount, creditAccount))
            .set("revision", 1)
            .set("sequence", 1)
            .set("event_name", name)
            .set("occurred_on", LocalDateTime.now())
            .set("aggregate_id", id)
            .set("aggregate_type", "Transaction")
            .build()

        val actual = transactionRegisteredMapper.map(record)!!

        assertEquals(id, actual.id)
        assertEquals(value, actual.amount.value)
        assertEquals(currency, actual.amount.currency)
        assertEquals(debitAccount, actual.debitAccount)
        assertEquals(creditAccount, actual.creditAccount)
    }

    @Test
    fun `GIVEN that I have a invalid RegisteredTransaction event, WHEN the map method is called, THEN return the event`() {
        val name = TransactionRegistered::class.simpleName!!
        val schema = SchemaMock.get(name)
        val record = GenericRecordBuilder(schema)
            .set("code", UUID.generateAsString())
            .set("payload", "")
            .set("revision", 1)
            .set("sequence", 1)
            .set("event_name", name)
            .set("occurred_on", LocalDateTime.now())
            .set("aggregate_id", "")
            .set("aggregate_type", "")
            .build()

        val actual = transactionRegisteredMapper.map(record)

        assertNull(actual)
    }

    @Test
    fun `GIVEN that I have an event other than TransactionRegistered, WHEN the map method is called, return null`() {
        val name = "EventTest"
        val schema = SchemaMock.get(name)
        val record = GenericRecordBuilder(schema)
            .set("code", UUID.generateAsString())
            .set("payload", "")
            .set("revision", 1)
            .set("sequence", 1)
            .set("event_name", name)
            .set("occurred_on", LocalDateTime.now())
            .set("aggregate_id", "")
            .set("aggregate_type", "")
            .build()

        val actual = transactionRegisteredMapper.map(record)

        assertNull(actual)
    }

    companion object {
        @JvmStatic
        fun transactionRegisteredProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    TransactionRegistered(
                        id = UUID.generateAsString(),
                        amount = Amount((10..10000).random().toBigDecimal(), Currency.BRL),
                        debitAccount = DEBIT_ACCOUNT_IN_BRL,
                        creditAccount = CREDIT_ACCOUNT_IN_BRL
                    )
                ),
                Arguments.of(
                    TransactionRegistered(
                        id = UUID.generateAsString(),
                        amount = Amount((10..10000).random().toBigDecimal(), Currency.USD),
                        debitAccount = DEBIT_ACCOUNT_IN_USD,
                        creditAccount = CREDIT_ACCOUNT_IN_USD
                    )
                )
            )
        }
    }
}