package financial.accounting.account.application.handlers

import financial.accounting.account.AccountBalanceBuilder
import financial.accounting.account.AccountBalancesSpy
import financial.accounting.account.AdjustBalanceBuilder
import financial.accounting.account.application.domain.models.AccountNumber
import financial.accounting.account.application.domain.models.TransactionId
import financial.accounting.account.application.exceptions.AccountBalanceNotFound
import financial.shared.testing.Amounts
import financial.shared.testing.FixedClock
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BalanceAdjustingHandlerTest {

    private val debit = AccountBalanceBuilder.debitAccount().build()

    private val credit = AccountBalanceBuilder.creditAccount().build()

    private val command = AdjustBalanceBuilder.fromDebitToCredit().build()

    @Test
    fun `handle when both accounts exist then adjusts them together under the transaction id`() {
        /** @Given balances holding the debit and the credit accounts at BRL 100.00 each */
        val balances = AccountBalancesSpy.holding(debit, credit)

        /** @When the adjustment of BRL 30 from the debit to the credit account is handled */
        BalanceAdjustingHandler(clock = FixedClock.clock, balances = balances).handle(command = command)

        /** @Then the debit account at BRL 70.00 and the credit account at BRL 130.00 are adjusted under t1 */
        val expected = Triple(
            debit.copy(balance = Amounts.brl(value = "70.00"), updatedAt = FixedClock.now),
            credit.copy(balance = Amounts.brl(value = "130.00"), updatedAt = FixedClock.now),
            TransactionId(value = "t1")
        )
        assertEquals(listOf(expected), balances.adjusted)
    }

    @Test
    fun `handle when the debit account does not exist then fails naming it without adjusting`() {
        /** @Given balances holding only the credit account 2.2.1.001 */
        val balances = AccountBalancesSpy.holding(credit)
        val handler = BalanceAdjustingHandler(clock = FixedClock.clock, balances = balances)

        /** @When the adjustment of BRL 30 from the debit to the credit account is handled */
        val actual = assertThrows<AccountBalanceNotFound> { handler.handle(command = command) }

        /** @Then the failure names the missing debit account 2.1.1.001 */
        assertEquals(AccountNumber(value = "2.1.1.001"), actual.number)

        /** @And no balance is adjusted */
        assertTrue(balances.adjusted.isEmpty())
    }

    @Test
    fun `handle when the credit account does not exist then fails naming it without adjusting`() {
        /** @Given balances holding only the debit account 2.1.1.001 */
        val balances = AccountBalancesSpy.holding(debit)
        val handler = BalanceAdjustingHandler(clock = FixedClock.clock, balances = balances)

        /** @When the adjustment of BRL 30 from the debit to the credit account is handled */
        val actual = assertThrows<AccountBalanceNotFound> { handler.handle(command = command) }

        /** @Then the failure names the missing credit account 2.2.1.001 */
        assertEquals(AccountNumber(value = "2.2.1.001"), actual.number)

        /** @And no balance is adjusted */
        assertTrue(balances.adjusted.isEmpty())
    }
}
