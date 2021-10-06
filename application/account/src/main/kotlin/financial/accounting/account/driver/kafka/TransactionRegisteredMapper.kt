package financial.accounting.account.driver.kafka

import financial.accounting.account.driver.policies.events.TransactionRegistered
import financial.shared.log.Logger
import financial.shared.mapper.JsonMapper
import financial.shared.streams.kafka.stream.KafkaEventMapper
import org.apache.avro.generic.GenericRecord

class TransactionRegisteredMapper(private val logger: Logger, private val jsonMapper: JsonMapper) : KafkaEventMapper {

    override val topic = "financial.accounting.bookkeeping.transaction-registered"

    override fun map(record: GenericRecord): TransactionRegistered? {
        if (record.schema.name != TransactionRegistered::class.simpleName) {
            return null
        }

        return try {
            jsonMapper.toObject(record.get("payload").toString(), TransactionRegistered::class)
        } catch (exception: Exception) {
            logger.loggerFor(this::class).error("Error mapping event - ${exception.message.toString()}")
            null
        }
    }
}