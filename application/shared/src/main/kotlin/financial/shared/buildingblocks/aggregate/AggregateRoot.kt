package financial.shared.buildingblocks.aggregate

import financial.shared.buildingblocks.entity.Entity
import financial.shared.valueobject.Version

interface AggregateRoot<T> : Entity {

    val modelVersion: Version
}