package financial.accounting.account.command.solve

import financial.shared.adapters.console.command.resolver.CommandHandler
import financial.shared.buildingblocks.domainevent.Event
import financial.shared.log.Logger
import financial.shared.policies.Failures
import financial.shared.policies.Policy

class SolveFailuresHandler(
    private val logger: Logger,
    private val failures: Failures,
    private val policies: Set<Policy<Event>>
) : CommandHandler<SolveFailures> {

    override fun handle(command: SolveFailures) {
        val failuresList = failures.list(command.limit)

        failuresList.forEach { it ->
            val clazz = it.policy.java
            val policy = policies.first { clazz.isAssignableFrom(it::class.java) }

            policy.handle(it.event)
            failures.remove(it.id)
        }

        logger.loggerFor(this::class).info("Number of processed failures: ${failuresList.count()}")
    }
}