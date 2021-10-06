package financial.shared.eventsourcing

import financial.shared.buildingblocks.valueobject.ValueObject

interface Event : ValueObject {

    fun type(): String

    fun revision(): Int
}