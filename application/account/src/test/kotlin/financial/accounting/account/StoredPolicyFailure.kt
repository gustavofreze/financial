package financial.accounting.account

data class StoredPolicyFailure(
    val error: String,
    val resent: Int,
    val payload: String,
    val eventName: String,
    val policyName: String
)
