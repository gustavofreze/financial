package financial.shared.domain.events

/**
 * Fact an aggregate raised, kept as part of its history.
 */
interface DomainEvent {
    /**
     * The revision of the shape of the fact, raised by one at every change of its fields.
     */
    val revision: Int
}
