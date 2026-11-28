package financial.shared.domain.exceptions

class UnsupportedCurrency(val value: String) : IllegalArgumentException()
