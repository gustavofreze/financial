package financial.shared.streams

interface StreamFactory {

    suspend fun build()
}