package financial.accounting.account.driven.troubleshoot

import financial.accounting.account.driver.policies.events.Event
import financial.shared.databases.DatabaseContext
import financial.shared.databases.DatabaseMapper
import financial.shared.mapper.JsonMapper
import financial.shared.policies.Failures.Failure
import financial.shared.policies.Policy
import java.sql.ResultSet
import kotlin.reflect.KClass

class FailureMapper(private val jsonMapper: JsonMapper) : DatabaseMapper<Failure> {

    @Suppress("UNCHECKED_CAST")
    override fun map(resultSet: ResultSet, context: DatabaseContext): Failure {
        val payload = resultSet.getString("payload")
        val eventName = resultSet.getString("event_name")
        val policyName = resultSet.getString("policy_name")

        return Failure(
            id = resultSet.getString("id"),
            event = jsonMapper.toObject(payload, Class.forName(eventName).kotlin) as Event,
            error = resultSet.getString("error"),
            policy = Class.forName(policyName).kotlin as KClass<out Policy<out Event>>,
            eventName = eventName,
            policyName = policyName,
        )
    }
}