package financial.accounting.bookkeeping.application.handlers

import financial.accounting.bookkeeping.TransactionsSpy
import financial.accounting.bookkeeping.application.commands.RegisterFinancialTransaction
import financial.accounting.bookkeeping.application.domain.models.AccountNumber
import financial.accounting.bookkeeping.application.domain.models.Transaction
import financial.shared.testing.Amounts
import financial.shared.testing.FixedClock
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FinancialTransactionRegisteringHandlerTest {

    @Test
    fun `handle when the order is valid then appends the registered transaction and notifies it`() {
        /** @Given a handler that appends to the transactions and records each notice */
        val noticed = mutableListOf<Transaction>()
        val transactions = TransactionsSpy()
        val handler = FinancialTransactionRegisteringHandler(
            clock = FixedClock.clock,
            notices = { transaction -> noticed.add(transaction) },
            transactions = transactions
        )

        /** @And an order to register BRL 2587 from 2.1.1.001 to 2.2.1.001 */
        val command = RegisterFinancialTransaction(
            amount = Amounts.brl(value = "2587"),
            debitAccount = AccountNumber(value = "2.1.1.001"),
            creditAccount = AccountNumber(value = "2.2.1.001")
        )

        /** @When the order is handled */
        handler.handle(command = command)

        /** @Then the appended transaction records the ordered amount */
        val transaction = transactions.appended.single()
        val recorded = transaction.recordedEvents.single()
        assertEquals(command.amount, recorded.event.amount)

        /** @And it records the ordered debit account */
        assertEquals(command.debitAccount, recorded.event.debitAccount)

        /** @And it records the ordered credit account */
        assertEquals(command.creditAccount, recorded.event.creditAccount)

        /** @And it occurred at the fixed instant */
        assertEquals(FixedClock.now, recorded.occurredOn)

        /** @And the registered transaction is noticed */
        assertEquals(listOf(transaction), noticed)
    }
}
