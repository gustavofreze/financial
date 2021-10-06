package financial.accounting.account.driven.troubleshoot

import financial.shared.databases.RelationalDatabase
import financial.shared.mapper.JsonMapper
import financial.shared.policies.Failures
import financial.shared.policies.Failures.Failure
import java.time.LocalDateTime

class Persistence(
    private val database: RelationalDatabase,
    private val jsonMapper: JsonMapper,
    private val failureMapper: FailureMapper
) : Failures {

    override fun add(failure: Failure) {
        database
            .bind("id", failure.id)
            .bind("error", failure.error)
            .bind("payload", jsonMapper.toJson(failure.event))
            .bind("event_name", failure.eventName)
            .bind("policy_name", failure.policyName)
            .upsert(Queries.INSERT)
    }

    override fun list(limit: Int): List<Failure> {
        return database
            .bind("limit", limit)
            .query(Queries.LIST)
            .fetchAll(failureMapper)
    }

    override fun remove(id: String) {
        database
            .bind("id", id)
            .bind("updated_at", LocalDateTime.now())
            .upsert(Queries.DELETE)
    }
}