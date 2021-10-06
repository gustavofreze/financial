package financial.shared.policies

import financial.shared.buildingblocks.domainevent.Event
import financial.shared.policies.Failures.Failure
import financial.shared.valueobject.UUID
import kotlin.reflect.KClass

abstract class Policy<T : Event> {

    abstract val failures: Failures
    abstract val policyClass: KClass<out Policy<T>>
    abstract val subscribedEvent: KClass<T>

    abstract fun doHandle(event: T)

    open fun handle(event: T) {
        try {
            doHandle(event)
        } catch (exception: Exception) {
            failures.add(
                Failure(
                    id = UUID.generateAsString(),
                    error = exception.stackTraceToString(),
                    event = event,
                    policy = policyClass,
                    eventName = event::class.qualifiedName ?: "Unknown event class",
                    policyName = policyClass.qualifiedName ?: "Unknown policy class"
                )
            )
        }
    }
}