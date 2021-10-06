package financial.accounting.bookkeeping.core.valueobject

import financial.shared.buildingblocks.entity.Identity
import financial.shared.buildingblocks.valueobject.ValueObject
import financial.shared.valueobject.UUID

data class TransactionId(override val value: String) : Identity, ValueObject {

    companion object {
        fun generate(): TransactionId = TransactionId(value = UUID.generateAsString())
    }
}