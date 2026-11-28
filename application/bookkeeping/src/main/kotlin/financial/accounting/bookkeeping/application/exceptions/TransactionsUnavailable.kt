package financial.accounting.bookkeeping.application.exceptions

class TransactionsUnavailable(override val cause: Exception) : IllegalStateException()
