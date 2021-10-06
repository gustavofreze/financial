package financial.shared.valueobject

import financial.shared.buildingblocks.valueobject.ValueObject
import java.util.UUID

object UUID : ValueObject {

    fun generateAsString(): String = UUID.randomUUID().toString()
}