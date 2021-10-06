package unit.financial.accounting.account.driven.repository

import commonvalues.Accounts.CREDIT_ACCOUNT_IN_USD
import commonvalues.Accounts.DEBIT_ACCOUNT_IN_USD
import financial.accounting.account.core.model.AccountBalance
import financial.accounting.account.core.valueobject.AccountId
import financial.accounting.account.driven.repository.AccountBalanceMapper
import financial.accounting.account.driven.repository.AccountBalanceNotFoundException
import financial.accounting.account.driven.repository.AccountBalanceRepository
import financial.accounting.account.driven.repository.Queries
import financial.shared.databases.RelationalDatabase
import financial.shared.valueobject.Amount
import financial.shared.valueobject.Currency
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.*
import java.time.LocalDateTime
import kotlin.test.assertEquals
import financial.accounting.account.core.model.AccountBalanceRepository as IAccountBalanceRepository

class AccountBalanceRepositoryTest {

    private lateinit var database: RelationalDatabase
    private lateinit var accountBalanceMapper: AccountBalanceMapper
    private lateinit var accountBalanceRepository: IAccountBalanceRepository

    @BeforeEach
    fun `Before each`() {
        database = mockk(relaxed = true, relaxUnitFun = true)
        accountBalanceMapper = AccountBalanceMapper()
        accountBalanceRepository = AccountBalanceRepository(
            database = database,
            accountBalanceMapper = accountBalanceMapper
        )
    }

    @AfterEach
    fun `After each`() {
        clearAllMocks()
    }

    @Test
    fun `GIVEN an account that exists, WHEN the pull method is called, THEN return the account data`() {
        val accountBalance = AccountBalance(
            identity = AccountId((1..10000).random()),
            number = CREDIT_ACCOUNT_IN_USD,
            balance = Amount((10..10000).random().toBigDecimal(), Currency.USD),
            updatedAt = LocalDateTime.now()
        )

        every {
            database
                .bind("number", accountBalance.number)
                .query(Queries.FIND)
                .fetchOne(accountBalanceMapper)
        } returns accountBalance

        val actual = accountBalanceRepository.pull(accountBalance.number)

        assertEquals(accountBalance.identity, actual.identity)
        assertEquals(accountBalance.balance.value, actual.balance.value)
        assertEquals(accountBalance.balance.currency, actual.balance.currency)
        assertEquals(accountBalance.updatedAt, actual.updatedAt)
    }

    @Test
    fun `GIVEN a non-existent account number, WHEN pull from that account, THEN return an account not found exception`() {
        val accountNumber = "0.0.0.1"

        every {
            database
                .bind("number", accountNumber)
                .query(Queries.FIND)
                .fetchOne(accountBalanceMapper)
        } returns null

        assertThrows<AccountBalanceNotFoundException>("Account balance not found for account number $accountNumber") {
            accountBalanceRepository.pull(accountNumber)
        }
    }

    @Test
    fun `GIVEN a valid account, WHEN the push method is called, THEN persist the account data without error`() {
        val accountBalance = AccountBalance(
            identity = AccountId((1..10000).random()),
            number = DEBIT_ACCOUNT_IN_USD,
            balance = Amount((10..10000).random().toBigDecimal(), Currency.USD),
            updatedAt = LocalDateTime.now()
        )

        every {
            database
                .bind("id", accountBalance.identity)
                .bind("balance", accountBalance.balance.value)
                .bind("updated_at", accountBalance.updatedAt)
                .upsert(Queries.UPDATE)
        }.answers { }

        assertDoesNotThrow {
            accountBalanceRepository.push(accountBalance)
        }
    }
}