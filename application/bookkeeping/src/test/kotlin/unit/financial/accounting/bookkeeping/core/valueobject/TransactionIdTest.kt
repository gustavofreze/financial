package unit.financial.accounting.bookkeeping.core.valueobject

import financial.accounting.bookkeeping.core.valueobject.TransactionId
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TransactionIdTest {

    @Test
    fun `GIVEN two valid identity, WHEN the value is equal between them, THEN they are equal`() {
        val transactionIdX = TransactionId.generate()
        val transactionIdY = TransactionId(value = transactionIdX.value)

        assertEquals(transactionIdX, transactionIdX)
        assertEquals(transactionIdY.value, transactionIdX.value)
    }
}