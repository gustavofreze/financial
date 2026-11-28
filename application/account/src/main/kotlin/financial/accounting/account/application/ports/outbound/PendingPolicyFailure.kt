package financial.accounting.account.application.ports.outbound

data class PendingPolicyFailure(val id: String, val payload: String, val policyName: String)
