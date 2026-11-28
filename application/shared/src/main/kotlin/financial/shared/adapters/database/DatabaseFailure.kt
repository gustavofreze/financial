package financial.shared.adapters.database

class DatabaseFailure(override val cause: Exception) : IllegalStateException()
