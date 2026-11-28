package financial.accounting.bookkeeping.application.domain.models

import java.util.UUID

data class TransactionId(val value: String) {

    companion object {
        fun generate(): TransactionId = TransactionId(value = UUID.randomUUID().toString())
    }
}
