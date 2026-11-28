package financial.shared.adapters.kafka

import org.junit.jupiter.api.Test
import java.net.ServerSocket
import kotlin.test.assertNull

class KafkaConnectClientTest {

    @Test
    fun `get when kafka connect cannot be reached then answers nothing`() {
        /** @Given a local port nothing listens on */
        val port = ServerSocket(0).use { socket -> socket.localPort }

        /** @When the connectors are requested from that port */
        val actual = KafkaConnectClient(url = "http://127.0.0.1:$port").get(path = "/connectors")

        /** @Then the client answers nothing */
        assertNull(actual)
    }
}
