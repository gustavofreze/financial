package financial.accounting.account.query.balance.database

import financial.accounting.account.AccountBalanceBuilder
import financial.accounting.account.AccountSchema
import financial.accounting.account.query.balance.Balance
import financial.accounting.account.query.balance.BalanceFindingUnavailable
import financial.shared.adapters.database.DatabaseFailure
import financial.shared.testing.Amounts
import financial.shared.testing.H2
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertIs

@Tag("Integration")
class DatabaseBalanceFindingTest {

    @Test
    fun `find all when accounts exist then reads every balance in account order`() {
        /** @Given the debit account at BRL 100.00 and the credit account at BRL 250.00 */
        val debit = AccountBalanceBuilder.debitAccount().build()
        val credit = AccountBalanceBuilder.creditAccount().withBalance(balance = Amounts.brl(value = "250.00")).build()

        /** @And a database storing the credit account before the debit account */
        val finding = DatabaseBalanceFinding(
            database = H2.database(
                *AccountSchema.statements,
                AccountSchema.balance(account = credit),
                AccountSchema.balance(account = debit)
            )
        )

        /** @When every balance is found */
        val actual = finding.findAll()

        /** @Then the balances come back in account number order */
        val expected = listOf(
            Balance(value = BigDecimal("100.00"), number = "2.1.1.001"),
            Balance(value = BigDecimal("250.00"), number = "2.2.1.001")
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `find all when the balances cannot be read then fails as unavailable`() {
        /** @Given a balance finding over a database with no schema */
        val finding = DatabaseBalanceFinding(database = H2.database())

        /** @When every balance is found */
        val actual = assertThrows<BalanceFindingUnavailable> { finding.findAll() }

        /** @Then the unavailable finding keeps the database failure as its cause */
        assertIs<DatabaseFailure>(actual.cause)
    }
}
