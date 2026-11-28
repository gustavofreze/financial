package financial.accounting.account.application.domain.models

import financial.accounting.account.AccountBalanceBuilder
import financial.shared.domain.exceptions.CurrencyMismatch
import financial.shared.domain.models.Amount
import financial.shared.domain.models.Currency
import financial.shared.testing.Amounts
import financial.shared.testing.FixedClock
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import kotlin.test.assertEquals

class AccountBalanceTest {

    private val balance = AccountBalanceBuilder.debitAccount().build()

    @Test
    fun `debit when the amount exceeds the balance then leaves it negative`() {
        /** @Given an amount of BRL 150 above the balance of BRL 100.00 */
        val amount = Amounts.brl(value = "150")

        /** @When the account is debited by that amount */
        val actual = balance.debit(at = FixedClock.now, amount = amount)

        /** @Then the account holds a negative balance of BRL 50.00 updated at the moment of the debit */
        assertEquals(balance.copy(balance = Amounts.brl(value = "-50.00"), updatedAt = FixedClock.now), actual)
    }

    @Test
    fun `credit when the currency matches then adds the amount`() {
        /** @Given an amount of BRL 100 in the currency of the balance of BRL 100.00 */
        val amount = Amounts.brl(value = "100")

        /** @When the account is credited by that amount */
        val actual = balance.credit(at = FixedClock.now, amount = amount)

        /** @Then the account holds BRL 200.00 updated at the moment of the credit */
        assertEquals(balance.copy(balance = Amounts.brl(value = "200.00"), updatedAt = FixedClock.now), actual)
    }

    @Test
    fun `debit when the currency differs then fails`() {
        /** @Given an amount of USD 1 against a balance in BRL */
        val amount = Amount(value = BigDecimal("1"), currency = Currency.USD)

        /** @When the account is debited by that amount */
        val actual = assertThrows<CurrencyMismatch> { balance.debit(at = FixedClock.now, amount = amount) }

        /** @Then the debit fails as a currency mismatch */
        assertEquals(CurrencyMismatch::class, actual::class)
    }
}
