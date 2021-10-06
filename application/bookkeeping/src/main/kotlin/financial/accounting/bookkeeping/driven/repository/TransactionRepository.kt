package financial.accounting.bookkeeping.driven.repository

import financial.accounting.bookkeeping.core.model.Transaction
import financial.accounting.bookkeeping.driven.repository.Queries.INSERT
import financial.shared.databases.RelationalDatabase
import financial.shared.mapper.JsonMapper
import financial.accounting.bookkeeping.core.model.TransactionRepository as ITransactionRepository

class TransactionRepository(
    private val database: RelationalDatabase,
    private val jsonMapper: JsonMapper
) : ITransactionRepository {

    override fun push(transaction: Transaction) {
        transaction.eventSourcing.recordedEvents
            .forEach {
                database
                    .bind("code", it.code)
                    .bind("payload", jsonMapper.toJson(it.event))
                    .bind("revision", it.revision)
                    .bind("sequence", it.sequence.value)
                    .bind("event_name", it.eventName)
                    .bind("occurred_on", it.occurredOn)
                    .bind("aggregate_id", it.aggregateId)
                    .bind("aggregate_type", it.aggregateType)
                    .upsert(INSERT)
            }
    }
}