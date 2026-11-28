package financial.accounting.account.application.ports.outbound

data class PolicyFailure(val cause: Exception, val payload: String, val eventName: String, val policyName: String)
