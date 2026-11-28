package financial.accounting.bookkeeping.driven.repository

import financial.accounting.bookkeeping.application.domain.models.Transaction
import financial.accounting.bookkeeping.application.exceptions.TransactionsUnavailable
import financial.accounting.bookkeeping.application.ports.outbound.Transactions
import financial.shared.adapters.database.Database
import financial.shared.adapters.database.TranslatedDatabase
import financial.shared.adapters.json.JsonMapper
import java.util.UUID

class EventStoreTransactions(database: Database, private val jsonMapper: JsonMapper) : Transactions {

    private val database = TranslatedDatabase(
        database = database,
        translate = { failure -> TransactionsUnavailable(cause = failure) }
    )

    override fun append(transaction: Transaction) {
        database.inTransaction { session ->
            transaction.recordedEvents.forEach { recorded ->
                val parameters = mapOf(
                    "code" to UUID.randomUUID().toString(),
                    "payload" to jsonMapper.toJson(value = TransactionRegisteredPayload.from(event = recorded.event)),
                    "revision" to recorded.event.revision,
                    "sequence" to recorded.sequence.value,
                    "event_name" to recorded.event.javaClass.simpleName,
                    "occurred_on" to recorded.occurredOn,
                    "aggregate_id" to transaction.id.value,
                    "aggregate_type" to Transaction::class.java.simpleName
                )
                session.update(sql = Queries.INSERT, parameters = parameters)
            }
        }
    }
}
