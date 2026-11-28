package financial.accounting.account.application.exceptions

class PolicyNotFound(val policyName: String) : IllegalStateException()
