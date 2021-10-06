package integration.financial.accounting.bookkeeping.core.model

import financial.accounting.bookkeeping.command.register.RegisterFinancialTransaction
import financial.accounting.bookkeeping.core.model.Status
import financial.accounting.bookkeeping.core.model.Transaction
import financial.accounting.bookkeeping.core.valueobject.TransactionId
import financial.accounting.bookkeeping.driven.repository.TransactionRepository
import financial.shared.databases.RelationalDatabase
import financial.shared.eventsourcing.EventSourcing
import financial.shared.eventsourcing.EventSourcingCapabilities
import financial.shared.mapper.JsonMapperFactory
import financial.shared.valueobject.Version
import integration.PostgreSql
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals
import financial.accounting.bookkeeping.core.model.TransactionRepository as ITransactionRepository

@Tag("Integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionTest {

    private lateinit var database: RelationalDatabase
    private lateinit var eventSourcing: EventSourcing<Transaction>
    private lateinit var transactionRepository: ITransactionRepository

    @BeforeAll
    fun `Before all`() {
        database = PostgreSql.load()
    }

    @BeforeEach
    fun `Before each`() {
        eventSourcing = EventSourcingCapabilities()
        transactionRepository = TransactionRepository(database = database, jsonMapper = JsonMapperFactory())
    }

    @ParameterizedTest
    @MethodSource("commandProvider")
    fun `GIVEN that I have a valid transaction, WHEN the recording method is called, THEN record the transaction and persist the record in an event table`(
        command: RegisterFinancialTransaction
    ) {
        val identity = TransactionId.generate()
        val transaction = Transaction(identity = identity, sequence = Version.first(), eventSourcing = eventSourcing)

        transaction.register(
            amount = command.amount,
            debitAccount = command.debitAccount,
            creditAccount = command.creditAccount
        )

        transactionRepository.push(transaction)

        assertEquals(identity.value, transaction.id)
        assertEquals(Status.REGISTERED, transaction.status)
        assertEquals(1, eventSourcing.recordedEvents.count())
    }

    companion object {
        @JvmStatic
        fun commandProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(RegisterFinancialTransaction()),
                Arguments.of(RegisterFinancialTransaction()),
                Arguments.of(RegisterFinancialTransaction())
            )
        }
    }
}