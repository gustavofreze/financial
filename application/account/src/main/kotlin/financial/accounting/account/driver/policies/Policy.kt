package financial.accounting.account.driver.policies

import financial.accounting.account.application.ports.outbound.PolicyFailure
import financial.accounting.account.application.ports.outbound.PolicyFailures
import financial.accounting.account.application.ports.outbound.PolicyReplay
import financial.shared.adapters.json.JsonMapper
import financial.shared.application.IntegrationEvent
import financial.shared.application.ReceivedEvent
import kotlin.reflect.KClass

abstract class Policy<E : IntegrationEvent>(
    override val name: String,
    private val event: KClass<E>,
    private val failures: PolicyFailures,
    private val jsonMapper: JsonMapper
) : PolicyReplay {

    fun handle(event: ReceivedEvent) {
        if (event.name != this.event.simpleName) {
            return
        }

        try {
            replay(payload = event.payload)
        } catch (exception: Exception) {
            val failure = PolicyFailure(
                cause = exception,
                payload = event.payload,
                eventName = this.event.java.name,
                policyName = name
            )
            failures.record(failure = failure)
        }
    }

    override fun replay(payload: String) = react(event = jsonMapper.toObject(json = payload, type = event))

    protected abstract fun react(event: E)
}
