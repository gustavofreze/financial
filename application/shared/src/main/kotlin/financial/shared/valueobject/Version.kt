package financial.shared.valueobject

import financial.shared.buildingblocks.valueobject.ValueObject

data class Version(val value: Long) : ValueObject {

    init {
        if (value < 0) error("Invalid version")
    }

    companion object {
        fun first(): Version = Version(1)
    }

    fun next(): Version = add(Version(1))

    private fun add(version: Version): Version = Version(version.value + value)
}