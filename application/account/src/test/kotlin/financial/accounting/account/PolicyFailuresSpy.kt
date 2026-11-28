package financial.accounting.account

import financial.accounting.account.application.ports.outbound.PendingPolicyFailure
import financial.accounting.account.application.ports.outbound.PolicyFailure
import financial.accounting.account.application.ports.outbound.PolicyFailures

class PolicyFailuresSpy private constructor(
    private val pending: List<PendingPolicyFailure>,
    private val resolveFailure: Exception?
) : PolicyFailures {

    val recorded = mutableListOf<PolicyFailure>()

    val resolved = mutableListOf<String>()

    val retained = mutableListOf<Pair<String, Exception>>()

    var requestedLimit: Int? = null
        private set

    override fun record(failure: PolicyFailure) {
        recorded.add(failure)
    }

    override fun pending(limit: Int): List<PendingPolicyFailure> {
        requestedLimit = limit
        return pending
    }

    override fun resolve(id: String) {
        resolveFailure?.let { failure -> throw failure }
        resolved.add(id)
    }

    override fun retain(id: String, cause: Exception) {
        retained.add(id to cause)
    }

    companion object {
        fun empty() = PolicyFailuresSpy(pending = emptyList(), resolveFailure = null)

        fun holding(vararg pending: PendingPolicyFailure) =
            PolicyFailuresSpy(pending = pending.toList(), resolveFailure = null)

        fun failingToResolve(failure: Exception, vararg pending: PendingPolicyFailure) =
            PolicyFailuresSpy(pending = pending.toList(), resolveFailure = failure)
    }
}
