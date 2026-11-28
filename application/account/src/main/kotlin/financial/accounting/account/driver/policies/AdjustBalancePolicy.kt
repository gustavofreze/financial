package financial.accounting.account.driver.policies

import financial.accounting.account.application.commands.AdjustBalance
import financial.accounting.account.application.domain.models.AccountNumber
import financial.accounting.account.application.domain.models.TransactionId
import financial.accounting.account.application.ports.inbound.BalanceAdjusting
import financial.accounting.account.application.ports.outbound.PolicyFailures
import financial.accounting.account.driver.policies.events.TransactionRegistered
import financial.shared.adapters.json.JsonMapper
import financial.shared.domain.models.Amount
import financial.shared.domain.models.Currency

class AdjustBalancePolicy(failures: PolicyFailures, private val adjusting: BalanceAdjusting, jsonMapper: JsonMapper) :
    Policy<TransactionRegistered>(
        name = NAME,
        event = TransactionRegistered::class,
        failures = failures,
        jsonMapper = jsonMapper
    ) {

    override fun react(event: TransactionRegistered) {
        val command = AdjustBalance(
            amount = Amount(value = event.amount.value, currency = Currency.from(code = event.amount.currency)),
            debitAccount = AccountNumber(value = event.debitAccount),
            creditAccount = AccountNumber(value = event.creditAccount),
            transactionId = TransactionId(value = event.id)
        )

        adjusting.handle(command = command)
    }

    private companion object {
        const val NAME = "financial.accounting.account.driver.policies.AdjustBalancePolicy"
    }
}
