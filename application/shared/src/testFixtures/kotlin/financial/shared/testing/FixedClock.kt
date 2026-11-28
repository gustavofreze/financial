package financial.shared.testing

import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

object FixedClock {

    val instant: Instant = Instant.parse("2026-01-15T10:30:00Z")

    val clock: Clock = Clock.fixed(instant, ZoneOffset.UTC)

    val now: OffsetDateTime = OffsetDateTime.ofInstant(instant, ZoneOffset.UTC)
}
