package financial.accounting.account.application.handlers

import financial.accounting.account.application.commands.SolveFailures
import financial.accounting.account.application.exceptions.PolicyNotFound
import financial.accounting.account.application.ports.inbound.FailuresSolving
import financial.accounting.account.application.ports.outbound.PolicyFailures
import financial.accounting.account.application.ports.outbound.PolicyReplay
import financial.accounting.account.application.ports.outbound.SolvingNotices

class FailuresSolvingHandler(
    private val notices: SolvingNotices,
    private val failures: PolicyFailures,
    policies: List<PolicyReplay>
) : FailuresSolving {

    private val policies = policies.associateBy(PolicyReplay::name)

    override fun handle(command: SolveFailures) {
        val pending = failures.pending(limit = command.limit)

        pending.forEach { failure ->
            try {
                val policy = policies[failure.policyName] ?: throw PolicyNotFound(policyName = failure.policyName)
                policy.replay(payload = failure.payload)
            } catch (exception: Exception) {
                failures.retain(id = failure.id, cause = exception)
                return@forEach
            }

            failures.resolve(id = failure.id)
        }

        notices.solved(count = pending.size)
    }
}
