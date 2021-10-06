package unit.financial.accounting.bookkeeping.driven.repository

import financial.accounting.bookkeeping.core.model.Transaction
import financial.accounting.bookkeeping.core.valueobject.TransactionId
import financial.accounting.bookkeeping.driven.repository.TransactionRepository
import financial.shared.databases.RelationalDatabase
import financial.shared.eventsourcing.EventSourcing
import financial.shared.mapper.JsonMapper
import financial.shared.valueobject.UUID
import financial.shared.valueobject.Version
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.LocalDateTime
import financial.accounting.bookkeeping.core.model.TransactionRepository as ITransactionRepository

class TransactionRepositoryTest {

    private lateinit var database: RelationalDatabase
    private lateinit var jsonMapper: JsonMapper
    private lateinit var transactionRepository: ITransactionRepository

    @BeforeEach
    fun `Before each`() {
        database = mockk(relaxed = true, relaxUnitFun = true)
        jsonMapper = mockk(relaxed = true)
        transactionRepository = TransactionRepository(database = database, jsonMapper = jsonMapper)
    }

    @AfterEach
    fun `After each`() {
        clearAllMocks()
    }

    @Test
    fun `GIVEN a valid transaction, WHEN the push method is called, THEN persist the transaction data without error`() {
        val version = Version.first()
        val identity = TransactionId.generate()

        val eventSourcing = mockk<EventSourcing<Transaction>> {
            every { recordedEvents } returns mutableListOf(
                mockk {
                    every { code } returns UUID.generateAsString()
                    every { event } returns mockk()
                    every { revision } returns 1
                    every { sequence } returns version.next()
                    every { eventName } returns String()
                    every { occurredOn } returns LocalDateTime.now()
                    every { aggregateId } returns identity.value
                    every { aggregateType } returns Transaction::class.java.simpleName
                },
            )
        }

        val transaction = Transaction(identity = identity, sequence = version, eventSourcing = eventSourcing)

        assertDoesNotThrow {
            transactionRepository.push(transaction)
        }
    }
}