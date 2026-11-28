package financial.accounting.account.driver.policies.events

import financial.shared.application.IntegrationEvent
import java.math.BigDecimal

data class TransactionRegistered(
    val id: String,
    val amount: TransactionAmount,
    val debitAccount: String,
    val creditAccount: String
) : IntegrationEvent {

    data class TransactionAmount(val value: BigDecimal, val currency: String)
}
