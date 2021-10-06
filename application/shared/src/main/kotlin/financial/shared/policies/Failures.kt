package financial.shared.policies

import financial.shared.buildingblocks.domainevent.Event
import kotlin.reflect.KClass

interface Failures {

    fun add(failure: Failure)

    fun list(limit: Int): List<Failure>

    fun remove(id: String)

    data class Failure(
        val id: String,
        val error: String,
        val event: Event,
        val policy: KClass<out Policy<out Event>>,
        val eventName: String,
        val policyName: String
    )
}