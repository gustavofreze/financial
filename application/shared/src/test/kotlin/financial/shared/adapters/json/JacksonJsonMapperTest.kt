package financial.shared.adapters.json

import financial.shared.AmountReceived
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JacksonJsonMapperTest {

    @Test
    fun `to object when the json has unknown fields then ignores them`() {
        /** @Given a json mapper */
        val mapper = JacksonJsonMapper()

        /** @When json carrying a currency the amount received lacks is read into it */
        val actual = mapper.toObject(json = """{"amount":3,"currency":"BRL"}""", type = AmountReceived::class)

        /** @Then the amount received is built from its known fields only */
        assertEquals(AmountReceived(amount = 3), actual)
    }

    @Test
    fun `to json when the value is a data class then writes its properties`() {
        /** @Given a json mapper */
        val mapper = JacksonJsonMapper()

        /** @When an amount received is written as json */
        val actual = mapper.toJson(value = AmountReceived(amount = 3))

        /** @Then the json holds its amount only */
        assertEquals("""{"amount":3}""", actual)
    }
}
