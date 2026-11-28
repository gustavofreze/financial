package financial.shared.adapters.kafka

import java.time.Duration

/**
 * Pause of the running thread between two attempts.
 */
fun interface Pause {
    /**
     * Pauses the running thread for the given duration.
     */
    fun pause(duration: Duration)
}
