package financial.accounting.account.core.valueobject

import financial.shared.buildingblocks.valueobject.ValueObject
import financial.shared.buildingblocks.entity.Identity

data class AccountId(override val value: Int) : Identity, ValueObject {

    init {
        if (value < 1) error("Invalid account id")
    }
}