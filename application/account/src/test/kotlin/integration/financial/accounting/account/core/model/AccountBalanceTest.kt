package integration.financial.accounting.account.core.model

import commonvalues.Accounts.CREDIT_ACCOUNT_IN_BRL
import commonvalues.Accounts.DEBIT_ACCOUNT_IN_BRL
import financial.accounting.account.driven.repository.AccountBalanceMapper
import financial.accounting.account.driven.repository.AccountBalanceRepository
import financial.shared.databases.RelationalDatabase
import financial.shared.valueobject.Amount
import financial.shared.valueobject.Currency
import integration.PostgreSql
import org.junit.jupiter.api.*
import kotlin.test.assertEquals
import financial.accounting.account.core.model.AccountBalanceRepository as IAccountBalanceRepository

@Tag("Integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountBalanceTest {

    private lateinit var database: RelationalDatabase
    private lateinit var accountBalanceRepository: IAccountBalanceRepository

    @BeforeAll
    fun `Before all`() {
        database = PostgreSql.load()
    }

    @BeforeEach
    fun `Before each`() {
        accountBalanceRepository = AccountBalanceRepository(
            database = database,
            accountBalanceMapper = AccountBalanceMapper()
        )
    }

    @Test
    fun `GIVEN a debit and credit operation has taken place, WHEN the push method is called, THEN update the values in the database`() {
        val amount = Amount((10..10000).random().toBigDecimal(), Currency.BRL)

        val debitAccount = accountBalanceRepository.pull(DEBIT_ACCOUNT_IN_BRL)
        val creditAccount = accountBalanceRepository.pull(CREDIT_ACCOUNT_IN_BRL)

        val expectedDebitAccount = debitAccount.debit(amount)
        accountBalanceRepository.push(expectedDebitAccount)

        val expectedCreditAccount = creditAccount.credit(amount)
        accountBalanceRepository.push(expectedCreditAccount)

        val updatedDebitAccount = accountBalanceRepository.pull(DEBIT_ACCOUNT_IN_BRL)
        val updatedCreditAccount = accountBalanceRepository.pull(CREDIT_ACCOUNT_IN_BRL)

        assertEquals(expectedDebitAccount.balance.value, updatedDebitAccount.balance.value)
        assertEquals(expectedCreditAccount.balance.value, updatedCreditAccount.balance.value)
    }
}