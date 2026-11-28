package financial.shared.domain.models

import financial.shared.domain.exceptions.InvalidVersion

data class Version(val value: Long) {

    init {
        if (value < 0) {
            throw InvalidVersion(value = value)
        }
    }

    fun next(): Version = Version(value = value + 1)

    companion object {
        fun initial(): Version = Version(value = 0)
    }
}
