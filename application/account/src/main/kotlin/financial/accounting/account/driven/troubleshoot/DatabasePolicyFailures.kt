package financial.accounting.account.driven.troubleshoot

import financial.accounting.account.application.exceptions.PolicyFailuresUnavailable
import financial.accounting.account.application.ports.outbound.PendingPolicyFailure
import financial.accounting.account.application.ports.outbound.PolicyFailure
import financial.accounting.account.application.ports.outbound.PolicyFailures
import financial.shared.adapters.database.Database
import financial.shared.adapters.database.TranslatedDatabase
import java.time.Clock
import java.time.OffsetDateTime
import java.util.UUID

class DatabasePolicyFailures(private val clock: Clock, database: Database, private val messages: FailureMessages) :
    PolicyFailures {

    private val mapper = PendingPolicyFailureMapper()

    private val database = TranslatedDatabase(
        database = database,
        translate = { failure -> PolicyFailuresUnavailable(cause = failure) }
    )

    override fun record(failure: PolicyFailure) {
        database.update(
            sql = Queries.INSERT,
            parameters = mapOf(
                "id" to UUID.randomUUID().toString(),
                "error" to messages.describe(failure = failure.cause),
                "payload" to failure.payload,
                "event_name" to failure.eventName,
                "policy_name" to failure.policyName
            )
        )
    }

    override fun pending(limit: Int): List<PendingPolicyFailure> =
        database.fetchAll(sql = Queries.PENDING, mapper = mapper, parameters = mapOf("limit" to limit))

    override fun resolve(id: String) {
        val parameters = mapOf("id" to id, "updated_at" to OffsetDateTime.now(clock))
        database.update(sql = Queries.RESOLVE, parameters = parameters)
    }

    override fun retain(id: String, cause: Exception) {
        val error = messages.describe(failure = cause)
        val parameters = mapOf("id" to id, "error" to error, "updated_at" to OffsetDateTime.now(clock))
        database.update(sql = Queries.RETAIN, parameters = parameters)
    }
}
