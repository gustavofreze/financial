package financial.shared.adapters.kafka

import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path
import java.time.Duration

class KafkaConnectClient(private val url: String) {

    private val requestTimeout = Duration.ofSeconds(30)

    private val client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()

    fun get(path: String): HttpResponse<String>? =
        send(request = HttpRequest.newBuilder(URI.create("$url$path")).timeout(requestTimeout).GET().build())

    fun post(body: Path, path: String): HttpResponse<String>? = send(
        request = HttpRequest.newBuilder(URI.create("$url$path"))
            .timeout(requestTimeout)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofFile(body))
            .build()
    )

    fun succeeded(response: HttpResponse<String>?): Boolean = response?.statusCode()?.div(other = 100) == 2

    fun send(request: HttpRequest): HttpResponse<String>? = try {
        client.send(request, HttpResponse.BodyHandlers.ofString())
    } catch (_: IOException) {
        null
    }
}
