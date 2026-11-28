package financial.accounting.account.driven.repository

import financial.accounting.account.AccountBalanceBuilder
import financial.accounting.account.AccountSchema
import financial.accounting.account.application.domain.models.AccountBalance
import financial.accounting.account.application.domain.models.AccountNumber
import financial.accounting.account.application.domain.models.TransactionId
import financial.accounting.account.application.exceptions.AccountBalancesUnavailable
import financial.accounting.account.application.exceptions.BalanceAdjustmentConflict
import financial.shared.adapters.database.DatabaseFailure
import financial.shared.domain.models.Version
import financial.shared.testing.Amounts
import financial.shared.testing.FixedClock
import financial.shared.testing.H2
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Tag("Integration")
class DatabaseAccountBalancesTest {

    private val debit = AccountBalanceBuilder.debitAccount().build()

    private val credit = AccountBalanceBuilder.creditAccount().build()

    private val database = H2.database(
        *AccountSchema.statements,
        AccountSchema.balance(account = debit),
        AccountSchema.balance(account = credit)
    )

    private val balances = DatabaseAccountBalances(database = database)

    private val transactionId = TransactionId(value = "t1")

    @Test
    fun `find when the account exists then maps its balance`() {
        /** @Given the number 2.1.1.001 of the stored debit account */
        val number = debit.number

        /** @When the balance of that account is found */
        val actual = assertNotNull(balances.find(number = number))

        /** @Then the found balance matches the stored debit account */
        assertEquals(debit.copy(updatedAt = actual.updatedAt), actual)

        /** @And it last changed at the stored instant */
        assertEquals(debit.updatedAt.toInstant(), actual.updatedAt.toInstant())
    }

    @Test
    fun `find when the account does not exist then is null`() {
        /** @Given the number 9.9.9.999 of no stored account */
        val number = AccountNumber(value = "9.9.9.999")

        /** @When the balance of that account is found */
        val actual = balances.find(number = number)

        /** @Then no balance is found */
        assertNull(actual)
    }

    @Test
    fun `adjust when the transaction was not applied then stores both balances with the next version`() {
        /** @Given the debit account debited and the credit account credited by BRL 30 */
        val debited = adjustedDebit()
        val credited = adjustedCredit()

        /** @When both balances are adjusted under the transaction t1 */
        balances.adjust(debited = debited, credited = credited, transactionId = transactionId)

        /** @Then the debit account holds BRL 70.00 */
        val debitedStored = assertNotNull(balances.find(number = debited.number))
        val creditedStored = assertNotNull(balances.find(number = credited.number))
        assertEquals(BigDecimal("70.00"), debitedStored.balance.value)

        /** @And the credit account holds BRL 130.00 */
        assertEquals(BigDecimal("130.00"), creditedStored.balance.value)

        /** @And both balances moved to version 1 */
        assertEquals(Version(value = 1), debitedStored.version)
        assertEquals(Version(value = 1), creditedStored.version)

        /** @And the transaction t1 is recorded as applied */
        assertEquals(listOf(transactionId.value), AccountSchema.adjustments(database = database))
    }

    @Test
    fun `adjust when the transaction was already applied then changes nothing`() {
        /** @Given both balances adjusted by BRL 30 under the transaction t1 */
        val debited = adjustedDebit()
        val credited = adjustedCredit()
        balances.adjust(debited = debited, credited = credited, transactionId = transactionId)

        /** @And the debit account debited again by its whole balance */
        val debitedAgain = debited.debit(at = FixedClock.now, amount = debited.balance)

        /** @When the adjustment is sent again under the same transaction t1 */
        balances.adjust(debited = debitedAgain, credited = credited, transactionId = transactionId)

        /** @Then the debit account still holds BRL 70.00 */
        assertEquals(BigDecimal("70.00"), balanceOf(number = debited.number))

        /** @And the credit account still holds BRL 130.00 */
        assertEquals(BigDecimal("130.00"), balanceOf(number = credited.number))
    }

    @Test
    fun `adjust when two transactions are applied then stores both adjustments`() {
        /** @Given both balances adjusted by BRL 30 under the transaction t1 */
        val debited = adjustedDebit()
        val credited = adjustedCredit()
        balances.adjust(debited = debited, credited = credited, transactionId = transactionId)

        /** @And both stored balances read back and moved again by BRL 10 */
        val amount = Amounts.brl(value = "10")
        val debitedAgain = assertNotNull(balances.find(number = debited.number))
            .debit(at = FixedClock.now, amount = amount)
        val creditedAgain = assertNotNull(balances.find(number = credited.number))
            .credit(at = FixedClock.now, amount = amount)

        /** @When the second adjustment is applied under the transaction t2 */
        balances.adjust(debited = debitedAgain, credited = creditedAgain, transactionId = TransactionId(value = "t2"))

        /** @Then the debit account holds BRL 60.00 */
        assertEquals(BigDecimal("60.00"), balanceOf(number = debited.number))

        /** @And the credit account holds BRL 140.00 */
        assertEquals(BigDecimal("140.00"), balanceOf(number = credited.number))
    }

    @Test
    fun `adjust when the debited balance is stale then fails and adjusts nothing`() {
        /** @Given both balances adjusted by BRL 30 under the transaction t1 */
        val debited = adjustedDebit()
        val credited = adjustedCredit()
        balances.adjust(debited = debited, credited = credited, transactionId = transactionId)

        /** @And a debit of BRL 10 on the debit account as it was before that adjustment */
        val stale = debited.debit(at = FixedClock.now, amount = Amounts.brl(value = "10"))

        /** @And the credit account as currently stored */
        val current = assertNotNull(balances.find(number = credited.number))

        /** @When the adjustment is applied under the transaction t2 */
        val actual = assertThrows<BalanceAdjustmentConflict> {
            balances.adjust(debited = stale, credited = current, transactionId = TransactionId(value = "t2"))
        }

        /** @Then the conflict names the debit account */
        assertEquals(debited.number, actual.number)

        /** @And the debit account still holds BRL 70.00 */
        assertEquals(BigDecimal("70.00"), balanceOf(number = debited.number))

        /** @And the credit account still holds BRL 130.00 */
        assertEquals(BigDecimal("130.00"), balanceOf(number = credited.number))

        /** @And only the transaction t1 is recorded as applied */
        assertEquals(listOf(transactionId.value), AccountSchema.adjustments(database = database))
    }

    @Test
    fun `adjust when the credited balance is stale then fails and adjusts nothing`() {
        /** @Given both balances adjusted by BRL 30 under the transaction t1 */
        val debited = adjustedDebit()
        val credited = adjustedCredit()
        balances.adjust(debited = debited, credited = credited, transactionId = transactionId)

        /** @And the debit account as currently stored */
        val current = assertNotNull(balances.find(number = debited.number))

        /** @And a credit of BRL 10 on the credit account as it was before that adjustment */
        val stale = credited.credit(at = FixedClock.now, amount = Amounts.brl(value = "10"))

        /** @When the adjustment is applied under the transaction t2 */
        val actual = assertThrows<BalanceAdjustmentConflict> {
            balances.adjust(debited = current, credited = stale, transactionId = TransactionId(value = "t2"))
        }

        /** @Then the conflict names the credit account */
        assertEquals(credited.number, actual.number)

        /** @And the debit account still holds BRL 70.00 */
        assertEquals(BigDecimal("70.00"), balanceOf(number = debited.number))

        /** @And the credit account still holds BRL 130.00 */
        assertEquals(BigDecimal("130.00"), balanceOf(number = credited.number))

        /** @And only the transaction t1 is recorded as applied */
        assertEquals(listOf(transactionId.value), AccountSchema.adjustments(database = database))
    }

    @Test
    fun `find when the balances cannot be read then fails as unavailable`() {
        /** @Given account balances over a database with no schema */
        val unavailable = DatabaseAccountBalances(database = H2.database())

        /** @When the balance of the debit account is found */
        val actual = assertThrows<AccountBalancesUnavailable> { unavailable.find(number = debit.number) }

        /** @Then the unavailable balances keep the database failure as their cause */
        assertIs<DatabaseFailure>(actual.cause)
    }

    private fun adjustedDebit(): AccountBalance = debit.debit(at = FixedClock.now, amount = Amounts.brl(value = "30"))

    private fun adjustedCredit(): AccountBalance =
        credit.credit(at = FixedClock.now, amount = Amounts.brl(value = "30"))

    private fun balanceOf(number: AccountNumber): BigDecimal =
        assertNotNull(balances.find(number = number)).balance.value
}
