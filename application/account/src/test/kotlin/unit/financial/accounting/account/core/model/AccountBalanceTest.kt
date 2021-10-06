package unit.financial.accounting.account.core.model

import commonvalues.Accounts.CREDIT_ACCOUNT_IN_BRL
import commonvalues.Accounts.DEBIT_ACCOUNT_IN_BRL
import financial.accounting.account.core.model.AccountBalance
import financial.accounting.account.core.valueobject.AccountId
import financial.shared.valueobject.Amount
import financial.shared.valueobject.Currency
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.test.assertEquals

class AccountBalanceTest {

    @Test
    fun `GIVEN I have a balance of BRL 100, where a debit of BRL 100 is made, THEN return the updated balance of BRL 0`() {
        val amount = Amount(value = BigDecimal(100.00), currency = Currency.BRL)
        val accountBalance = AccountBalance(
            identity = AccountId(1),
            number = DEBIT_ACCOUNT_IN_BRL,
            balance = amount,
            updatedAt = LocalDateTime.now()
        )

        val actual = accountBalance.debit(amount)
        val expected = Amount(value = BigDecimal.ZERO, currency = Currency.BRL)

        assertEquals(expected.value.setScale(2), actual.balance.value)
        assertEquals(expected.currency, actual.balance.currency)
    }

    @Test
    fun `GIVEN I have a balance of BRL 100, where a credit of BRL 100 is made, THEN return the updated balance of BRL 200`() {
        val amount = Amount(value = BigDecimal(100.00), currency = Currency.BRL)
        val accountBalance = AccountBalance(
            identity = AccountId(1),
            number = CREDIT_ACCOUNT_IN_BRL,
            balance = amount,
            updatedAt = LocalDateTime.now()
        )

        val actual = accountBalance.credit(amount)
        val expected = Amount(value = BigDecimal(200.00), currency = Currency.BRL)

        assertEquals(expected.value.setScale(2), actual.balance.value)
        assertEquals(expected.currency, actual.balance.currency)
    }

    @Test
    fun `GIVEN I have a balance of BRL 0, where a debit of BRL 150 is made, THEN I return the updated balance of negative BRL 150`() {
        val amount = Amount(value = BigDecimal(150.00), currency = Currency.BRL)
        val accountBalance = AccountBalance(
            identity = AccountId(1),
            number = CREDIT_ACCOUNT_IN_BRL,
            balance = Amount(BigDecimal.ZERO, Currency.BRL),
            updatedAt = LocalDateTime.now()
        )

        val actual = accountBalance.debit(amount)
        val expected = Amount(value = BigDecimal(-150.00), currency = Currency.BRL)

        assertEquals(expected.value.setScale(2), actual.balance.value)
        assertEquals(expected.currency, actual.balance.currency)
    }
}