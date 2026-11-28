package financial.shared.domain.models

import financial.shared.domain.exceptions.UnsupportedCurrency

enum class Currency {
    BRL,
    USD;

    companion object {
        fun from(code: String): Currency =
            entries.firstOrNull { it.name == code } ?: throw UnsupportedCurrency(value = code)
    }
}
