package integration.financial.accounting.account.driven.troubleshoot

import commonvalues.Accounts.CREDIT_ACCOUNT_IN_BRL
import commonvalues.Accounts.DEBIT_ACCOUNT_IN_BRL
import financial.accounting.account.driven.troubleshoot.FailureMapper
import financial.accounting.account.driven.troubleshoot.Persistence
import financial.accounting.account.driver.policies.AdjustBalancePolicy
import financial.accounting.account.driver.policies.events.TransactionRegistered
import financial.shared.databases.RelationalDatabase
import financial.shared.mapper.JsonMapper
import financial.shared.mapper.JsonMapperFactory
import financial.shared.policies.Failures
import financial.shared.policies.Failures.Failure
import financial.shared.valueobject.Amount
import financial.shared.valueobject.Currency
import financial.shared.valueobject.UUID
import integration.PostgreSql
import org.junit.jupiter.api.*
import java.math.BigDecimal
import kotlin.test.assertEquals

@Tag("Integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersistenceTest {

    private val limit = 100
    private lateinit var database: RelationalDatabase
    private lateinit var jsonMapper: JsonMapper
    private lateinit var persistence: Failures

    @BeforeAll
    fun `Before all`() {
        database = PostgreSql.load()
        jsonMapper = JsonMapperFactory()
    }

    @BeforeEach
    fun `Before each`() {
        persistence = Persistence(
            database = database,
            jsonMapper = jsonMapper,
            failureMapper = FailureMapper(jsonMapper)
        )
    }

    @AfterEach
    fun `After each`() {
        val list = persistence.list(limit)

        list.forEach { persistence.remove(it.id) }
    }

    @Test
    fun `GIVEN that the record has been inserted into the table, WHEN all records are retrieved, THEN the returned list will only have a single item, AND the persisted information must be the same as the inserted one`() {
        val record = failure()

        persistence.add(record)

        val actual = persistence.list(limit)

        assertEquals(1, actual.count())
        assertEquals(record, actual.first())
    }

    @Test
    fun `GIVEN that the record has been inserted into the table, WHEN it is requested to remove it through the identifier, THEN it should no longer be listed`() {
        val record = failure()

        persistence.add(record)
        persistence.remove(record.id)

        val actual = persistence.list(limit).count()

        assertEquals(0, actual)
    }

    private fun failure(): Failure {
        val event = TransactionRegistered(
            id = UUID.generateAsString(),
            amount = Amount(BigDecimal(100.00), Currency.BRL),
            debitAccount = DEBIT_ACCOUNT_IN_BRL,
            creditAccount = CREDIT_ACCOUNT_IN_BRL
        )
        val policy = AdjustBalancePolicy::class

        return Failure(
            id = UUID.generateAsString(),
            error = "Stacktrace",
            event = event,
            policy = policy,
            eventName = event::class.qualifiedName!!,
            policyName = policy.qualifiedName!!
        )
    }
}