package unit.financial.accounting.account.core.valueobject

import financial.accounting.account.core.valueobject.AccountId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class AccountIdTest {

    @Test
    fun `GIVEN two valid identity, WHEN the value is equal between them, THEN they are equal`() {
        val accountIdX = AccountId(value = 1)
        val accountIdY = AccountId(value = accountIdX.value)

        assertEquals(accountIdX, accountIdX)
        assertEquals(accountIdY.value, accountIdX.value)
    }

    @Test
    fun `GIVEN I have an invalid accountId, WHEN instantiated, THEN return an error`() {
        assertThrows<Exception>("Invalid account id") {
            AccountId(0)
        }
    }
}