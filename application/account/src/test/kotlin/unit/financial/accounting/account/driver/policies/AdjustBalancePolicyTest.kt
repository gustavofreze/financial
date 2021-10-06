package unit.financial.accounting.account.driver.policies

import commonvalues.Accounts.CREDIT_ACCOUNT_IN_USD
import commonvalues.Accounts.DEBIT_ACCOUNT_IN_USD
import financial.accounting.account.driver.policies.AdjustBalancePolicy
import financial.accounting.account.driver.policies.events.TransactionRegistered
import financial.shared.policies.Failures
import financial.shared.policies.Policy
import financial.shared.valueobject.Amount
import financial.shared.valueobject.Currency
import financial.shared.valueobject.UUID
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import mock.AdjustBalanceBusMock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AdjustBalancePolicyTest {

    private lateinit var failures: Failures
    private lateinit var commandBus: AdjustBalanceBusMock
    private lateinit var adjustBalancePolicy: Policy<TransactionRegistered>

    @BeforeEach
    fun `Before Each`() {
        failures = mockk(relaxed = true)
        commandBus = AdjustBalanceBusMock()
        adjustBalancePolicy = AdjustBalancePolicy(failures = failures, commandBus = commandBus)
    }

    @Test
    fun `GIVEN an instance of the AdjustBalancePolicy, WHEN requested the event interested by the policy, THEN the TransactionRegistered class must be returned`() {
        val subscribedEvent = adjustBalancePolicy.subscribedEvent

        assertTrue(this::adjustBalancePolicy.isInitialized)
        assertEquals(TransactionRegistered::class, subscribedEvent)
    }

    @Test
    fun `GIVEN that I have an TransactionRegistered event, WHEN the policy runs, THEN a single command must be dispatched`(): Unit =
        runBlocking {
            val event = transactionRegistered()

            adjustBalancePolicy.handle(event)

            val command = commandBus.commands().first()

            assertEquals(1, commandBus.commands().count())
            assertEquals(event.id, command.id)
            assertEquals(event.amount.value, command.balance.value)
            assertEquals(event.amount.currency, command.balance.currency)
            assertEquals(event.debitAccount, command.debitAccount)
            assertEquals(event.creditAccount, command.creditAccount)
        }

    private fun transactionRegistered(): TransactionRegistered {
        return TransactionRegistered(
            id = UUID.generateAsString(),
            amount = Amount((10..10000).random().toBigDecimal(), Currency.USD),
            debitAccount = DEBIT_ACCOUNT_IN_USD,
            creditAccount = CREDIT_ACCOUNT_IN_USD
        )
    }
}