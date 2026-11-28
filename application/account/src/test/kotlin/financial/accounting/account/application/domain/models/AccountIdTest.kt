package financial.accounting.account.application.domain.models

import financial.accounting.account.application.domain.exceptions.InvalidAccountId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class AccountIdTest {

    @Test
    fun `account id when the value is below one then fails with the value`() {
        /** @Given a value of minus three below the minimum of one */
        val value = -3

        /** @When an account id is built from that value */
        val actual = assertThrows<InvalidAccountId> { AccountId(value = value) }

        /** @Then the failure carries the rejected value */
        assertEquals(-3, actual.value)
    }
}
