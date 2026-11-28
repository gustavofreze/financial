package financial.accounting.account

import financial.shared.application.ReceivedEvent

class ReceivedEventBuilder private constructor(private val currency: String) {

    fun withCurrency(currency: String): ReceivedEventBuilder = ReceivedEventBuilder(currency = currency)

    fun build(): ReceivedEvent = ReceivedEvent(
        name = "TransactionRegistered",
        payload = """{"id":"t1","amount":{"value":30,"currency":"$currency"},"status":1,""" +
            """"debitAccount":"2.1.1.001","creditAccount":"2.2.1.001"}"""
    )

    companion object {
        fun transactionRegistered(): ReceivedEventBuilder = ReceivedEventBuilder(currency = "BRL")
    }
}
