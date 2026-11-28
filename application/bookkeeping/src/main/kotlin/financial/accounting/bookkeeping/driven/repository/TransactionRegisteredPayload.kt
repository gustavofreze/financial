package financial.accounting.bookkeeping.driven.repository

import financial.accounting.bookkeeping.application.domain.events.TransactionRegistered
import java.math.BigDecimal

data class TransactionRegisteredPayload(
    val id: String,
    val amount: AmountPayload,
    val status: Int,
    val debitAccount: String,
    val creditAccount: String
) {

    data class AmountPayload(val value: BigDecimal, val currency: String)

    companion object {
        fun from(event: TransactionRegistered) = TransactionRegisteredPayload(
            id = event.id.value,
            amount = AmountPayload(value = event.amount.value, currency = event.amount.currency.name),
            status = event.status.code,
            debitAccount = event.debitAccount.value,
            creditAccount = event.creditAccount.value
        )
    }
}
